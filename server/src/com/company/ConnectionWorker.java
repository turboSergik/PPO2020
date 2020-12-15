package com.company;

import com.sergey.codeeditorPPO2020.models.File;
import com.sergey.codeeditorPPO2020.models.RunMetaInfo;

import java.io.*;
import java.net.Socket;

public class ConnectionWorker implements Runnable {
    private final Socket clientSocket;

    private ObjectInputStream inputStream = null;

    public ConnectionWorker(Socket socket) {
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Can't get input stream");
        }

        while (true) {
            try {
                final RunMetaInfo runMetaInfo = (RunMetaInfo) inputStream.readObject();

                final Runtime rt = Runtime.getRuntime();

                final Process clearDir = rt.exec("rm -r executionDir");
                clearDir.waitFor();

                final Process createDir = rt.exec("mkdir executionDir");
                createDir.waitFor();

                for (final File file : runMetaInfo.getFiles()) {
                    file.setSourceCode(file.getSourceCode().replace("\\", "\\\\"));
                    file.setSourceCode(file.getSourceCode().replace("'", "\""));

                    String[] cmdline = { "sh", "-c", "echo " + "'" + file.getSourceCode() + "'" + " > " + "executionDir/" + file.getName()};
                    Process saveFile = Runtime.getRuntime().exec(cmdline);
                    saveFile.waitFor();
                }

                final String[] cmdlineRun;
                if (runMetaInfo.getInterpreter().equals("Python 3"))
                    cmdlineRun = new String[]{"sh", "-c", "cd executionDir && python3 " + runMetaInfo.getRunFile()};
                else
                    cmdlineRun = new String[]{"sh", "-c", "cd executionDir && python " + runMetaInfo.getRunFile()};

                final Process run = rt.exec(cmdlineRun);

                final BufferedReader stdInput = new BufferedReader(new InputStreamReader(run.getInputStream()));
                final BufferedReader stdError = new BufferedReader(new InputStreamReader(run.getErrorStream()));
                run.waitFor();

                String s;
                while ((s = stdInput.readLine()) != null) {
                    clientSocket.getOutputStream().write(s.getBytes());
                    clientSocket.getOutputStream().write("\n".getBytes());
                    System.out.println(s);
                }
                while ((s = stdError.readLine()) != null) {
                    clientSocket.getOutputStream().write(s.getBytes());
                    clientSocket.getOutputStream().write("\n".getBytes());
                    System.out.println(s);
                }

                clientSocket.getOutputStream().flush();
                clientSocket.getOutputStream().close();

                break;
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }

        System.out.println("ConnectionWorker stopped");
    }
}

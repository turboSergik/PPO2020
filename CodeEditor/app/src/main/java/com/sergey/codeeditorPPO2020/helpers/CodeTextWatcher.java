package com.sergey.codeeditorPPO2020.helpers;

import android.content.Context;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;

import com.sergey.codeeditorPPO2020.R;
import com.sergey.codeeditorPPO2020.models.Block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodeTextWatcher implements TextWatcher {
    private List<String> SYSTEM = Arrays.asList("if", "return", "import", "with", "while", "for", "in", "as", "+=", "=", "-", "//", "/", "!=", "==", "<", ">", "+", "*", "**");
    private List<Character> SEPARATORS = Arrays.asList('(', ')', ':', '[', ']', '{', '}', '.', '\'', ',');
    final List<ForegroundColorSpan> spans = new ArrayList<>();
    final Context context;

    public CodeTextWatcher(Context context) {
        this.context = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        for (ForegroundColorSpan foregroundColorSpan : spans)
            s.removeSpan(foregroundColorSpan);

        List<Block> lines = new ArrayList<>();

        for (int i = 0, left = 0; i <= s.length(); ++i) {
            if (i == s.length() || s.charAt(i) == '\n') {
                if (i - left > 0)
                    lines.add(new Block(left, i, s.subSequence(left, i)));

                left = i + 1;
            }
        }

        for (final Block line : lines) {
            List<Block> blocks = new ArrayList<>();

            boolean[] ignore = new boolean[s.length()];
            for (int i = line.start, last = -1; i < line.end; ++i) {
                if (last != -1)
                    ignore[last] = true;

                if (s.charAt(i) == '\'') {
                    if (last == -1) {
                        last = i;
                    } else {
                        final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.text));
                        spans.add(foregroundColorSpan);

                        s.setSpan(foregroundColorSpan, last, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        last = -1;
                    }
                }

                if (last != -1)
                    ignore[last] = true;
            }

            for (int i = line.start, left = line.start; i <= line.end; ++i) {
                if (i == s.length() || ignore[i] ||  s.charAt(i) <= 32 || SEPARATORS.contains(s.charAt(i))) {
                    if (i - left > 0)
                        blocks.add(new Block(left, i, s.subSequence(left, i)));

                    left = i + 1;
                }
            }

            if (blocks.size() > 0 && s.charAt(blocks.get(0).start) == '#') {
                final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.commentCode));
                spans.add(foregroundColorSpan);

                s.setSpan(foregroundColorSpan, line.start, line.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                continue;
            }

            if (blocks.size() > 0 && blocks.get(0).text.equals("def")) {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.functions));
                spans.add(foregroundColorSpan);

                s.setSpan(foregroundColorSpan, blocks.get(0).start, blocks.get(0).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (blocks.size() > 1) {
                    foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.defFunction));
                    spans.add(foregroundColorSpan);

                    s.setSpan(foregroundColorSpan, blocks.get(1).start, blocks.get(1).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                for (int i = 2; i < blocks.size(); ++i) {
                    foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.arguments));
                    spans.add(foregroundColorSpan);

                    s.setSpan(foregroundColorSpan, blocks.get(i).start, blocks.get(i).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                continue;
            }

            for (int i = 0; i < blocks.size(); ++i) {
                if ((s.length() != blocks.get(i).end && s.charAt(blocks.get(i).end) == '(') ||
                        (i > 0 && blocks.get(i - 1).text.equals("def"))) {
                    final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.functions));
                    spans.add(foregroundColorSpan);

                    s.setSpan(foregroundColorSpan, blocks.get(i).start, blocks.get(i).end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            for (final Block block : blocks) {
                if (SYSTEM.contains(block.text)) {
                    final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.systemToken));
                    spans.add(foregroundColorSpan);

                    s.setSpan(foregroundColorSpan, block.start, block.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (isNumber(block.text)) {
                    final ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.numbers));
                    spans.add(foregroundColorSpan);

                    s.setSpan(foregroundColorSpan, block.start, block.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }

    private boolean isNumber(CharSequence a) {
        if (a.length() == 0)
            return false;
        if (a.length() == 1 && a.charAt(0) == '-')
            return false;

        for (int i = a.charAt(0) == '-' ? 1 : 0; i < a.length(); ++i) {
            if (a.charAt(i) < '0' || a.charAt(i) > '9')
                return false;
        }

        return true;
    }
}

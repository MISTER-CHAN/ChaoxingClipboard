package com.misterchan.chaoxingclipboard;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String URL_PREFIX_DO_WORK = "https://mooc1.chaoxing.com/mooc2/work/dowork?courseId=";

    private static final String JS_ANSWERS = "" +
            "(() => {" +
            "    let number = %d, answer = \"%s\";" +
            "    let result = \"\";" +
            "    let questionLi = document.getElementsByClassName(questionClassName)[number - 1];" +
            "    switch (questionLi.getAttribute(\"typename\")) {" +
            "        case \"单选题\":" +
            "            var answerBgs = questionLi.getElementsByClassName(\"clearfix answerBg\");" +
            "            answer = answer.replace(/\\s/g, \"\").replace(/[;。；]$/, \"\");" +
            "            var value = questionLi.children[2].getAttribute(\"value\");" +
            "            for (let i = 0; i < answerBgs.length; ++i) {" +
            "                let answerP = answerBgs[i].getElementsByClassName(\"fl answer_p\")[0].textContent.trim().replace(/\\s/g, \"\").replace(/[;。；]$/, \"\");" +
            "                let c = String.fromCharCode(65 + i);" +
            "                result += c + \" \" + answerP;" +
            "                if (answerP == answer) {" +
            "                    if (c != value) {" +
            "                        answerBgs[i].click();" +
            "                    }" +
            "                    result += \" ✓\";" +
            "                }" +
            "                result += \"\\n\";" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case \"多选题\":" +
            "            var answerBgs = questionLi.getElementsByClassName(\"clearfix answerBg\");" +
            "            answer = answer.replace(/\\s/g, \"\").replace(/。$/, \"\");" +
            "            var value = questionLi.children[2].getAttribute(\"value\");" +
            "            for (let i = 0; i < answerBgs.length; ++i) {" +
            "                let answerP = answerBgs[i].getElementsByClassName(\"fl answer_p\")[0].textContent.trim().replace(/\\s/g, \"\").replace(/。$/, \"\");" +
            "                let c = String.fromCharCode(65 + i);" +
            "                result += c + \" \" + answerP;" +
            "                let b = answer.includes(answerP);" +
            "                if (b ^ value.includes(c)) {" +
            "                    answerBgs[i].click();" +
            "                }" +
            "                if (b) {" +
            "                    result += \" ✓\";" +
            "                }" +
            "                result += \"\\n\";" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case \"判断题\":" +
            "            var answerBgs = questionLi.getElementsByClassName(\"clearfix answerBg\");" +
            "            var value = questionLi.children[2].getAttribute(\"value\");" +
            "            if ([\"√\", \"对\", \"正确\"].includes(answer)) {" +
            "                if (value != \"true\") {" +
            "                    answerBgs[0].click();" +
            "                }" +
            "                result += \"对\";" +
            "            } else if ([\"×\", \"错\", \"错误\"].includes(answer)) {" +
            "                if (value != \"false\") {" +
            "                    answerBgs[1].click();" +
            "                }" +
            "                result += \"错\";" +
            "            }" +
            "            break;" +
            "        case \"填空题\":" +
            "            var e = editors[number - 1];" +
            "            var answers = [], sep = \"\";" +
            "            if (e.length > 1 &&" +
            "                (  /^\\[(?:\".*?\",?)+\\]$/.test(answer) && (answers = eval(answer)).length > 1" +
            "                || (sep = answer.match(/[;、；]\\s*/)) != null && (answers = answer.split(sep)).length == e.length" +
            "                )" +
            "            ) {" +
            "                for (let i = 0; i < e.length; ++i) {" +
            "                    UE.instants[\"ueditorInstant\" + e[i]].setContent(answers[i]);" +
            "                }" +
            "            } else {" +
            "                UE.instants[\"ueditorInstant\" + e[0]].setContent(answer);" +
            "            }" +
            "            result += answer;" +
            "            break;" +
            "        case \"简答题\":" +
            "            var e = editors[number - 1];" +
            "            UE.instants[\"ueditorInstant\" + e[0]].setContent(answer);" +
            "            result += answer;" +
            "            break;" +
            "    }" +
            "    return result;" +
            "})()";

    private static final String JS_QUESTIONS = "" +
            "if (typeof questionClassNames == \"undefined\") {" +
            "    var questionClassNames = [\"marBom50 questionLi\", \"padBom50 questionLi\"];" +
            "    var questionClassName = \"\";" +
            "}" +
            "let editors = [];" +
            "(() => {" +
            "    let questionLis = [];" +
            "    if (questionClassName == \"\") {" +
            "        questionClassNames.forEach((qcn, i) => {" +
            "            questionLis = document.getElementsByClassName(qcn);" +
            "            if (questionLis.length > 0) {" +
            "                questionClassName = qcn;" +
            "                return false;" +
            "            }" +
            "        });" +
            "    } else {" +
            "        questionLis = document.getElementsByClassName(questionClassName);" +
            "    }" +
            "    if (questionLis.length == 0) {" +
            "        return \"\";" +
            "    }" +
            "    let questions = [], answers = [];" +
            "    let editorId = 0;" +
            "    for (let i = 0; i < questionLis.length; ++i) {" +
            "        questions.push(questionLis[i].getElementsByClassName(\"mark_name colorDeep\")[0].textContent.match(/(?<=\\d+\\. \\(.+\\) )[\\s\\S]+/)[0]);" +
            "        var answer = \"\";" +
            "        switch (questionLis[i].getAttribute(\"typename\")) {" +
            "            case \"单选题\":" +
            "            case \"多选题\":" +
            "                var answerBgs = questionLis[i].getElementsByClassName(\"clearfix answerBg\"), answer = \"\";" +
            "                for (let j = 0; j < answerBgs.length; ++j) {" +
            "                    answer += String.fromCharCode(65 + j) + \" \" + answerBgs[j].getElementsByClassName(\"fl answer_p\")[0].textContent.trim() + \"\\n\";" +
            "                }" +
            "                answer = answer.slice(0, -1);" +
            "                break;" +
            "            case \"判断题\":" +
            "                answer = \"对？错？\";" +
            "                break;" +
            "            case \"填空题\":" +
            "                answer = \" \";" +
            "                var last = questionLis[i].getElementsByClassName(\"edui-editor  edui-default\").length + editorId;" +
            "                editors[i] = [];" +
            "                while (editorId < last) {" +
            "                    editors[i].push(editorId++);" +
            "                }" +
            "                break;" +
            "            case \"简答题\":" +
            "                answer = \" \";" +
            "                editors[i] = [editorId++];" +
            "                break;" +
            "            default:" +
            "                answer = \" \";" +
            "                break;" +
            "        }" +
            "        answers.push(answer);" +
            "    }" +
            "    return questions.join(\",,\") + \",,,\" + answers.join(\",,\");" +
            "})()";

    private Button bAnswer;
    private ClipboardManager clipboardManager;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llQuestions, llControl;
    private ScrollView svQuestions;
    private String[] questions;
    private WebView webView;

    public void answer(View view) {
        if (clipboardManager.hasPrimaryClip()) {

            // Answer
            answer(number, clipboardManager.getPrimaryClip().getItemAt(0).getText().toString());

            // Copy next question
            if (number < questions.length) {
                setNumber(number + 1);
                copyQuestion(number, questions[number - 1]);
                View v = llQuestions.getChildAt(number - 1);
                int y = v.getTop();
                if (svQuestions.getScrollY() + svQuestions.getHeight() < y + v.getHeight()) {
                    svQuestions.smoothScrollTo(0, y);
                }
            }
        }
    }

    /**
     * Answer a question.
     */
    @SuppressLint("DefaultLocale")
    private void answer(int number, String answer) {
        webView.evaluateJavascript(String.format(JS_ANSWERS, number, answer
                        .replace("\n", "")
                        .replace("\"", "\\\"")),
                value -> ((TextView) (llQuestions.getChildAt(number - 1)).findViewById(R.id.tv_answer)).setText(value.substring(1, value.length() - 1).replace("\\n", "\n")));
    }

    public void copyQuestion(View v) {
        setNumber((int) v.getTag());
        copyQuestion(number, ((TextView) v).getText().toString());
        bAnswer.setEnabled(true);
    }

    /**
     * Copy a question.
     */
    private void copyQuestion(int number, String question) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", question));
        Toast.makeText(this,  String.format("已复制第 %d 题题目", number), Toast.LENGTH_SHORT).show();
    }

    /**
     * Get all the questions from web.
     */
    private void matchWork() {
        webView.evaluateJavascript(JS_QUESTIONS, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!"null".equals(value)) {
                    String[] qAndA = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\u003C", "<").split(",,,");
                    if (qAndA.length == 2) {
                        // qAndA[0] - all the questions.
                        // qAndA[1] - all the answers.
                        questions = qAndA[0].split(",,");
                        showQuestions(questions, qAndA[1].split(",,"));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            if (webView.getVisibility() == View.GONE) {
                llControl.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
            return;
        }
        super.onBackPressed();
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bAnswer = findViewById(R.id.b_answer);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        layoutInflater = LayoutInflater.from(this);
        llControl = findViewById(R.id.ll_control);
        llQuestions = findViewById(R.id.ll_questions);
        svQuestions = findViewById(R.id.sv_questions);

        webView = findViewById(R.id.wv);
        WebSettings ws = webView.getSettings();
        ws.setAllowFileAccess(true);
        ws.setAppCacheEnabled(false);
        ws.setBlockNetworkImage(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setDatabaseEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setJavaScriptEnabled(true);
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.getSettings().setBlockNetworkImage(false);
                if (url.startsWith(URL_PREFIX_DO_WORK)) {
                    matchWork();
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                webView.getSettings().setBlockNetworkImage(true);
                if (!url.startsWith(URL_PREFIX_DO_WORK)) {
                    llQuestions.removeAllViews();
                    bAnswer.setEnabled(false);
                    number = 0;
                }
                super.onPageStarted(view, url, favicon);
            }
        });

        webView.loadUrl("https://i.chaoxing.com/");
    }

    /**
     * Set the current question number and change the text color.
     */
    private void setNumber(int number) {
        if (this.number > 0) {
            ((TextView) llQuestions.getChildAt(this.number - 1).findViewById(R.id.tv_question)).setTextColor(Color.BLACK);
        }
        this.number = number;
        ((TextView) llQuestions.getChildAt(this.number - 1).findViewById(R.id.tv_question)).setTextColor(Color.RED);
    }

    public void showHideQuestionsView(View v) {
        llControl.setVisibility(8 - llControl.getVisibility());
        webView.setVisibility(8 - webView.getVisibility());
    }

    /**
     * Show the questions.
     */
    private void showQuestions(String[] questions, String[] answers) {
        for (int i = 0; i < questions.length; ++i) {
            LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.question, null);

            // Question
            TextView tvQuestion = layout.findViewById(R.id.tv_question);
            int number = i + 1;
            tvQuestion.setText(number + " " + questions[i]);
            tvQuestion.setTag(number);

            // Answer
            ((TextView) layout.findViewById(R.id.tv_answer)).setText(answers[i]);

            llQuestions.addView(layout.findViewById(R.id.ll_question));
        }
    }
}
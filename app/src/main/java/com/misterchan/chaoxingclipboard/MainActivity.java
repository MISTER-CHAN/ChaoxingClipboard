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

    private static final String COPIED_ANSWER = "已复制第 %d 题选项";
    private static final String COPIED_QUESTION = "已复制第 %d 题题目";

    private static final String URL_PREFIX_DO_WORK = "https://mooc1.chaoxing.com/mooc2/work/dowork";
    private static final String URL_PREFIX_CHAPTER = "https://mooc1.chaoxing.com/mycourse/studentstudy";

    private static final String JS_ANSWERS = "" +
            "(() => {" +
            "    const CORRECT = /(?:√|对|正确)$/, INCORRECT = /(?:×|错|错误)$/;" +
            "    let number = %d, answer = '%s';" +
            "    let result = '';" +
            "    let questionLi = document.getElementsByClassName('questionLi')[number - 1];" +
            "    switch (questionLi.getAttribute('typename')) {" +
            "        case '单选题':" +
            "            var answerBgs = questionLi.getElementsByClassName('clearfix answerBg');" +
            "            answer = answer.replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "            var value = questionLi.children[2].getAttribute('value');" +
            "            for (let i = 0; i < answerBgs.length; ++i) {" +
            "                let answerP = answerBgs[i].getElementsByClassName('fl answer_p')[0].textContent.trim().replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "                let c = String.fromCharCode(65 + i);" +
            "                result += c + ' ' + answerP;" +
            "                if (answerP == answer) {" +
            "                    if (c != value) {" +
            "                        answerBgs[i].click();" +
            "                    }" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case '多选题':" +
            "            var answerBgs = questionLi.getElementsByClassName('clearfix answerBg');" +
            "            answer = answer.replace(/\\s/g, '');" +
            "            var value = questionLi.children[2].getAttribute('value');" +
            "            for (let i = 0; i < answerBgs.length; ++i) {" +
            "                let answerP = answerBgs[i].getElementsByClassName('fl answer_p')[0].textContent.trim().replace(/\\s/g, '').replace(/[;。；]$/, '');" +
            "                let c = String.fromCharCode(65 + i);" +
            "                result += c + ' ' + answerP;" +
            "                let b = answer.includes(answerP);" +
            "                if (b ^ value.includes(c)) {" +
            "                    answerBgs[i].click();" +
            "                }" +
            "                if (b) {" +
            "                    result += ' ✓';" +
            "                }" +
            "                result += '\\n';" +
            "            }" +
            "            result = result.slice(0, -1);" +
            "            break;" +
            "        case '判断题':" +
            "            var answerBgs = questionLi.getElementsByClassName('clearfix answerBg');" +
            "            var value = questionLi.children[2].getAttribute('value');" +
            "            if (CORRECT.test(answer)) {" +
            "                if (value != 'true') {" +
            "                    answerBgs[0].click();" +
            "                }" +
            "                result += '对';" +
            "            } else if (INCORRECT.test(answer)) {" +
            "                if (value != 'false') {" +
            "                    answerBgs[1].click();" +
            "                }" +
            "                result += '错';" +
            "            }" +
            "            break;" +
            "        case '填空题':" +
            "            var e = editors[number - 1];" +
            "            var answers = [], sep = '';" +
            "            if (e.length > 1 &&" +
            "                (  /^\\[(?:'.*?',?)+\\]$/.test(answer) && (answers = eval(answer)).length > 1" +
            "                || (sep = answer.match(/[;、；]\\s*/)) != null && (answers = answer.split(sep)).length == e.length" +
            "                )" +
            "            ) {" +
            "                for (let i = 0; i < e.length; ++i) {" +
            "                    UE.instants['ueditorInstant' + e[i]].setContent(answers[i]);" +
            "                }" +
            "            } else {" +
            "                UE.instants['ueditorInstant' + e[0]].setContent(answer);" +
            "            }" +
            "            result += answer;" +
            "            break;" +
            "        case '简答题':" +
            "            var e = editors[number - 1];" +
            "            UE.instants['ueditorInstant' + e[0]].setContent(answer);" +
            "            result += answer;" +
            "            break;" +
            "    }" +
            "    return result;" +
            "})()";

    private static final String JS_QUESTIONS = "" +
            "var editors = [];" +
            "(() => {" +
            "    let questionLis = [];" +
            "    questionLis = document.getElementsByClassName('questionLi');" +
            "    if (questionLis.length == 0) {" +
            "        return '';" +
            "    }" +
            "    let questions = [], answers = [];" +
            "    let editorId = 0;" +
            "    for (let i = 0; i < questionLis.length; ++i) {" +
            "        questions.push(questionLis[i].getElementsByClassName('mark_name colorDeep')[0].textContent.match(/(?<=\\d+\\. \\(.+\\) )[\\s\\S]+/)[0]);" +
            "        let answer = '  ';" +
            "        switch (questionLis[i].getAttribute('typename')) {" +
            "            case '单选题':" +
            "            case '多选题':" +
            "                var answerBgs = questionLis[i].getElementsByClassName('clearfix answerBg');" +
            "                answer = '';" +
            "                for (let j = 0; j < answerBgs.length; ++j) {" +
            "                    answer += String.fromCharCode(65 + j) + ' ' + answerBgs[j].getElementsByClassName('fl answer_p')[0].textContent.trim() + '\\n';" +
            "                }" +
            "                answer = answer.slice(0, -1);" +
            "                break;" +
            "            case '判断题':" +
            "                answer = '对？错？';" +
            "                break;" +
            "            case '填空题':" +
            "                answer = ' ';" +
            "                var last = questionLis[i].getElementsByClassName('edui-editor  edui-default').length + editorId;" +
            "                editors[i] = [];" +
            "                while (editorId < last) {" +
            "                    editors[i].push(editorId++);" +
            "                }" +
            "                break;" +
            "            case '简答题':" +
            "                answer = ' ';" +
            "                editors[i] = [editorId++];" +
            "                break;" +
            "            default:" +
            "                answer = ' ';" +
            "                break;" +
            "        }" +
            "        answers.push(answer);" +
            "    }" +
            "    return questions.join(',,') + ',,,' + answers.join(',,');" +
            "})()";

    private static final String JS_QUESTIONS_CHAPTER = "" +
            "var editors = [];" +
            "(() => {" +
            "    let doc = document.getElementById('iframe').contentDocument.getElementsByTagName('iframe')[0].contentDocument.getElementById('frame_content').contentDocument;" +
            "    let timus = doc.getElementsByClassName('TiMu');" +
            "    if (timus.length == 0) {" +
            "        return '';" +
            "    }" +
            "    let questions = [], answers = [];" +
            "    let editorId = 0;" +
            "    for (let i = 0; i < timus.length; ++i) {" +
            "        let qAndA = timus[i].getElementsByClassName('clearfix');" +
            "        let question = qAndA[1].textContent.match(/(【.+】)(.+)（\\d+\\.\\d+分）/);" +
            "        questions.push(question[2]);" +
            "        let answer = '  ';" +
            "        switch (question[1]) {" +
            "            case '【单选题】':" +
            "            case '【多选题】':" +
            "                var lis = timus[i].children[1].children[0].children;" +
            "                for (let j = 0; j < lis.length; ++j) {\n" +
            "                    answer += String.fromCharCode(65 + j) + ' ' + lis[j].getElementsByClassName('fl after')[0].textContent.trim() + '\\n';" +
            "                }" +
            "                answer = answer.slice(0, -1);" +
            "                break;" +
            "            case '【填空题】':" +
            "                answer = ' ';" +
            "                var last = timus[i].getElementsByClassName('edui-editor  edui-default').length + editorId;" +
            "                editors[i] = [];" +
            "                while (editorId < last) {" +
            "                    editors[i].push(editorId++);" +
            "                }" +
            "                break;" +
            "            case '【判断题】':" +
            "                answer = '对？错？';" +
            "                break;" +
            "            default:" +
            "                answer = ' ';" +
            "                break;" +
            "        }" +
            "        answers.push(answer);" +
            "    }" +
            "    return questions.join(',,') + ',,,' + answers.join(',,');" +
            "})();";

    private static final String JS_SKIP_VIDEO = "javascript:" +
            "(() => {" +
            "    let frames = document.getElementById('iframe').contentDocument.getElementsByTagName('iframe');" +
            "    for (let frame of frames) {" +
            "        let video = frame.contentDocument.getElementById('video_html5_api');" +
            "        if (video != null) {" +
            "            video.currentTime = video.duration;" +
            "        }" +
            "    }" +
            "})();";

    private Button bAnswer;
    private Button bMatchChapter;
    private ClipboardManager clipboardManager;
    private int number = 0;
    private LayoutInflater layoutInflater;
    private LinearLayout llChapter;
    private LinearLayout llControl;
    private LinearLayout llQuestions;
    private ScrollView svQuestions;
    private String[] questions;
    private WebView webView;

    private final WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            webView.getSettings().setBlockNetworkImage(false);
            if (url.startsWith(URL_PREFIX_DO_WORK)) {
                matchWork();
            } else if (url.startsWith(URL_PREFIX_CHAPTER)) {
                bringControlToFront(llChapter);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            webView.getSettings().setBlockNetworkImage(true);
            if (!url.startsWith(URL_PREFIX_DO_WORK) && !url.startsWith(URL_PREFIX_CHAPTER)) {
                llQuestions.removeAllViews();
                bAnswer.setEnabled(false);
                number = 0;
            }
            super.onPageStarted(view, url, favicon);
        }
    };

    private final ValueCallback<String> onReceiveQAndAsCallback = value -> {
        if (!"null".equals(value)) {
            String[] qAndA = value.substring(1, value.length() - 1).replace("\\n", "\n").replace("\\u003C", "<").split(",,,");
            if (qAndA.length == 2) {
                // qAndA[0] - all the questions.
                // qAndA[1] - all the answers.
                questions = qAndA[0].split(",,");
                showQuestions(questions, qAndA[1].split(",,"));
                bringControlToFront(bAnswer);
                return;
            }
        }
        bMatchChapter.setEnabled(true);
    };

    /**
     * Answer a question.
     */
    @SuppressLint("DefaultLocale")
    public void answer(View view) {
        if (clipboardManager.hasPrimaryClip()) {

            // Answer
            webView.evaluateJavascript(
                    String.format(JS_ANSWERS, number,
                            clipboardManager.getPrimaryClip().getItemAt(0).getText().toString()
                                    .trim().replace("\n", "").replace("'", "\\'")),
                    value -> {
                        if ("null".equals(value)) {
                            return;
                        }
                        ((TextView) (llQuestions.getChildAt(number - 1)).findViewById(R.id.tv_answer))
                                .setText(value.substring(1, value.length() - 1).replace("\\n", "\n"));

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
                    });
        }
    }

    private void bringControlToFront(View view) {
        bAnswer.setVisibility(View.GONE);
        llChapter.setVisibility(View.GONE);
        view.setVisibility(View.VISIBLE);
    }

    public void copyAnswer(View v) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", ((TextView) v).getText()));
        Toast.makeText(this, String.format(COPIED_ANSWER, number), Toast.LENGTH_SHORT).show();
    }

    public void copyQuestion(View v) {
        int number = (int) v.getTag();
        setNumber(number);
        copyQuestion(number, questions[number - 1]);
        bAnswer.setEnabled(true);
    }

    /**
     * Copy a question.
     */
    private void copyQuestion(int number, String question) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("Label", question));
        Toast.makeText(this, String.format(COPIED_QUESTION, number), Toast.LENGTH_SHORT).show();
    }

    public void matchChapterWork(View view) {
        view.setEnabled(false);
        webView.evaluateJavascript(JS_QUESTIONS_CHAPTER, onReceiveQAndAsCallback);
    }

    /**
     * Get all the questions from web.
     */
    private void matchWork() {
        webView.evaluateJavascript(JS_QUESTIONS, onReceiveQAndAsCallback);
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
        bMatchChapter = findViewById(R.id.b_match_chapter);
        clipboardManager = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE));
        layoutInflater = LayoutInflater.from(this);
        llChapter = findViewById(R.id.ll_chapter);
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
        ws.setUseWideViewPort(true);
        ws.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setWebViewClient(webViewClient);

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
        llControl.setVisibility(llControl.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        webView.setVisibility(webView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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

    public void skipVideo(View v) {
        webView.loadUrl(JS_SKIP_VIDEO);
        llControl.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
    }
}
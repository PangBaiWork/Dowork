package com.pangbai.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pangbai.terminal.TerminalSession;
import com.pangbai.terminal.TerminalSessionClient;


public final class SuperTerminalView extends TerminalView {

    public TerminalSession mTerminalSession = null;
    ClipboardManager clipboardManager;
    ClipData clipData;
    // Terminal
    private ExtraKeysView mkeys;
    private Activity mContext = null;
    private SuperTerminalView thiz = null;
    private TerminalSessionClient mTerminalSessionClient = null;
    private TerminalViewClient mTerminalViewClient = null;
    private boolean set_done = false;
    private boolean run_done = false;

    public SuperTerminalView(Activity context) {
        this(context, (AttributeSet) null);
        this.mContext = context;
        this.thiz = this;
        mTerminalSessionClient = new TSC();
        mTerminalViewClient = new TVC();
       //触摸
       // if (Command.roll) {
            setFocusable(true);
            setFocusableInTouchMode(true);
       // }
        clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);


    }

    public SuperTerminalView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public boolean setProcess(String cmd, String cwd, String[] argv, String[] envp, int rows) {
        if (set_done != false)
            return false;
        mTerminalSession = new TerminalSession(cmd, cwd, argv, envp, rows, mTerminalSessionClient);
        return true;
    }
    public void setKeyView(ExtraKeysView keys){
        this.mkeys=keys;
    }

    public boolean runProcess() {
        if (run_done) {

            return false;
        }
        setTerminalViewClient(mTerminalViewClient);
        attachSession(mTerminalSession);
        return false;
    }

    private class TSC implements TerminalSessionClient {

        @Override
        public void onColorsChanged(TerminalSession session) {
        }

        @Override
        public void onCopyTextToClipboard(TerminalSession session, String text) {
            ClipData mClipData = ClipData.newPlainText("Label", text);
            clipboardManager.setPrimaryClip(mClipData);
        }

        @Override
        public void onTitleChanged(TerminalSession changedSession) {
        }

        @Override
        public void onTextChanged(TerminalSession changedSession) {
            onScreenUpdated();
        }

        @Override
        public void onTerminalCursorStateChange(boolean state) {
        }

        @Override
        public void onBell(TerminalSession session) {
        }

        @Override
        public void onSessionFinished(TerminalSession finishedSession) {

            Handler mHander = new Handler(Looper.getMainLooper());
            mHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ViewGroup) thiz.getParent()).removeView(thiz);
                    mContext.finish();
                    mContext = null;
                    thiz = null;
                    mTerminalSession = null;
                    mTerminalSessionClient = null;
                    mTerminalViewClient = null;
                    //	ActivityTo.startActivity(mContext,main.class,true);
                }
            }, 2 * 1000);
        }

        @Override
        public void onPasteTextFromClipboard(TerminalSession session) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null) {
                CharSequence paste = clipData.getItemAt(0).getText();
                if (!TextUtils.isEmpty(paste)) mTermSession.mEmulator.paste(paste.toString());
            }
        }

        @Override
        public Integer getTerminalCursorStyle() {
            return null;
        }

        @Override
        public void logStackTrace(String tag, Exception e) {
        }

        @Override
        public void logError(String tag, String message) {
        }

        @Override
        public void logVerbose(String tag, String message) {
        }

        @Override
        public void logWarn(String tag, String message) {
        }

        @Override
        public void logDebug(String tag, String message) {
        }

        @Override
        public void logStackTraceWithMessage(String tag, String message, Exception e) {
        }

        @Override
        public void logInfo(String tag, String message) {
        }

        @Override
        public void setTerminalShellPid(TerminalSession session, int pid) {
        }

    }

    private class TVC implements TerminalViewClient {

        @Override
        public boolean readShiftKey() {
            return false;
        }


        @Override
        public boolean onKeyUp(int keyCode, KeyEvent e) {
            return false;
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {


            return false;
        }

        @Override
        public void onSingleTapUp(MotionEvent e) {
            //if (Command.roll) {

                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
           // }
        }

        @Override
        public float onScale(float scale) {
            return 0;
        }

        @Override
        public boolean onLongPress(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
            return false;
        }

        @Override
        public void onEmulatorSet() {
        }

        @Override
        public void logError(String tag, String message) {
        }

        @Override
        public void logVerbose(String tag, String message) {
        }

        @Override
        public void logWarn(String tag, String message) {
        }

        @Override
        public void logDebug(String tag, String message) {
        }

        @Override
        public void logStackTraceWithMessage(String tag, String message, Exception e) {
        }

        @Override
        public void logStackTrace(String tag, Exception e) {
        }

        @Override
        public void logInfo(String tag, String message) {
        }

        @Override
        public boolean readControlKey() {
            return (mkeys != null && mkeys.readSpecialButton(ExtraKeysView.SpecialButton.CTRL));
        }

        @Override
        public boolean readAltKey() {
            return (mkeys != null && mkeys.readSpecialButton(ExtraKeysView.SpecialButton.ALT));
        }


        @Override
        public boolean readFnKey() {
            return (mkeys != null &&mkeys.readSpecialButton(ExtraKeysView.SpecialButton.FN));

        }

        @Override
        public boolean isTerminalViewSelected() {
            return true;
        }

        @Override
        public boolean shouldUseCtrlSpaceWorkaround() {
            return false;
        }

        @Override
        public boolean shouldEnforceCharBasedInput() {
            return false;
        }

        @Override
        public boolean shouldBackButtonBeMappedToEscape() {
            return false;
        }

        @Override
        public void copyModeChanged(boolean copyMode) {
        }
		/*

		 private boolean handleVirtualKeys(int keyCode, KeyEvent event, boolean down) {
		 InputDevice inputDevice = event.getDevice();
		 if (inputDevice != null && inputDevice.getKeyboardType() == InputDevice.KEYBOARD_TYPE_ALPHABETIC) {
		 // Do not steal dedicated buttons from a full external keyboard.
		 return false;
		 } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
		 mVirtualControlKeyDown = down;
		 return true;
		 } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
		 mVirtualFnKeyDown = down;
		 return true;
		 }
		 return false;
		 }
		 */
    }

}






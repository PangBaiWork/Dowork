package com.pangbai.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

import com.pangbai.dowork.TermActivity;
import com.pangbai.dowork.preference.TermPreference;
import com.pangbai.dowork.service.mainService;
import com.pangbai.terminal.TerminalSession;
import com.pangbai.terminal.TerminalSessionClient;


public final class SuperTerminalView extends TerminalView {

    public TerminalSession mTerminalSession = null;
    ClipboardManager clipboardManager;
    ClipData clipData;
    // Terminal
    private ExtraKeysView mkeys;
    private TermActivity mTermActivity = null;
    public static SuperTerminalView terminal = null;
    public TerminalSessionClient mTerminalSessionClient = null;
    private TerminalViewClient mTerminalViewClient = null;
    private boolean set_done = false;
    private boolean run_done = false;
    static int  currentsize=30;
    public int[] textSizes;
    TermPreference termPreference;


    public SuperTerminalView(TermActivity context) {
        this(context, (AttributeSet) null);

        this.mTermActivity = context;
        this.terminal = this;
        mTerminalSessionClient = new TSC();
        mTerminalViewClient = new TVC();
       //触摸

       // if (Command.roll) {
            setFocusable(true);
            setFocusableInTouchMode(true);
       // }
        clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        //配置文件准备
        termPreference=mTermActivity.mTermSetting;
        textSizes=  getDefaultFontSizes(context);
        currentsize= termPreference.getIntStoredAsString(termPreference.fontSize,textSizes[0]);
        setTextSize(currentsize);

    }


    public SuperTerminalView(Context context, AttributeSet attributes) {
        super(context, attributes);
        mTerminalSessionClient = new TSC();
        mTerminalViewClient = new TVC();
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        textSizes=getDefaultFontSizes(context);
        setTextSize(textSizes[0]);
    }

    public static int[] getDefaultFontSizes(Context context) {
        float dipInPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        int[] sizes = new int[3];
        // This is a bit arbitrary and sub-optimal. We want to give a sensible default for minimum font size
        // to prevent invisible text due to zoom be mistake:
        sizes[1] = (int) (4f * dipInPixels); // min
        // http://www.google.com/design/spec/style/typography.html#typography-line-height
        int defaultFontSize = Math.round(12 * dipInPixels);
        // Make it divisible by 2 since that is the minimal adjustment step:
        if (defaultFontSize % 2 == 1) defaultFontSize--;
        sizes[0] = defaultFontSize; // default
        sizes[2] = 256; // max
        return sizes;
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
            if (clipboardManager==null)
                return;
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
        public void onSessionFinished(TerminalSession finishedSession,int DelayTime) {


            Handler mHander = new Handler(Looper.getMainLooper());
            mHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTerminalSession = null;
                   // mTerminalSessionClient = null;
                  //  mTerminalViewClient = null;
                //   SuperTerminalView.this=null;
                 /*  View parent=(View)getParent();
                   if (parent!=null)
                       parent.setVisibility(View.GONE);*/
                    SuperTerminalView.this.setVisibility(GONE);


                 if (isViewInActivity(SuperTerminalView.this)){
                    // ((ViewGroup) thiz.getParent()).removeView(thiz);
                     mTerminalSessionClient = null;
                     mTerminalViewClient = null;
                     mkeys.removeAllViews();
                     mkeys=null;
                     mTermActivity.finish();
                    mTermActivity=null;}else {

                     Intent mIntent=new Intent(getContext(), mainService.class);
                     mainService.isCmdRunning=false;
                     if (TerminalSession.EXITCODE==0)
                        mIntent.putExtra("action",mainService.action_success);
                     else
                         mIntent.putExtra("action",mainService.action_failed);
                     getContext().startService(mIntent);
                 }



                }
            }, DelayTime * 1000);
        }

        @Override
        public void onPasteTextFromClipboard(TerminalSession session) {
           if (clipboardManager==null)
               return;
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
        public void logDebug(String tag, String message) {
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
                if (mTermActivity==null)
                    return;
                InputMethodManager inputMethodManager = (InputMethodManager) mTermActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
           // }
        }

   /*  @Override
      public float onScale(float scale) {
            return 0;
        }*/
   @Override
   public float onScale(float scale) {
       if (scale < 0.9f || scale > 1.1f) {
           boolean increase = scale > 1.f;
           int fontSize =termPreference.getIntStoredAsString(termPreference.fontSize,textSizes[0]);
           fontSize += (increase ? 1 : -1) * 2;
           fontSize = Math.max(textSizes[1], Math.min(fontSize, textSizes[2]));
           termPreference.setIntStoredAsString(termPreference.fontSize,fontSize,false);
           setTextSize(fontSize);
           //setFontSize(fontSize);
          // changeFontSize(increase);
           return 1.0f;
       }
       return scale;
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






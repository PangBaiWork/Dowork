package com.pangbai.terminal;

/**
 * The interface for communication between {@link TerminalSession} and its client. It is used to
 * send callbacks to the client when {@link TerminalSession} changes or for sending other
 * back data to the client like logs.
 */
public interface TerminalSessionClient {

    default void onTextChanged(TerminalSession changedSession) {

    }

    default void onTitleChanged(TerminalSession changedSession) {

    }

    void onSessionFinished(TerminalSession finishedSession, int DelayTime);

    default void onCopyTextToClipboard(TerminalSession session, String text) {

    }

    default void onPasteTextFromClipboard(TerminalSession session) {

    }

    default void onBell(TerminalSession session) {

    }

    default void onColorsChanged(TerminalSession session) {

    }

    default void onTerminalCursorStateChange(boolean state) {

    }

    void setTerminalShellPid(TerminalSession session, int pid);


    default Integer getTerminalCursorStyle() {
        return null;
    }


    default void logError(String tag, String message) {

    }

    default void logWarn(String tag, String message) {

    }

    default void logInfo(String tag, String message) {

    }

    default void logDebug(String tag, String message) {

    }

    default void logVerbose(String tag, String message) {

    }

    default void logStackTraceWithMessage(String tag, String message, Exception e) {

    }

    default void logStackTrace(String tag, Exception e) {

    }

}

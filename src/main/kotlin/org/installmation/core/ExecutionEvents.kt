package org.installmation.core

/**
 * Only required to be fired by test harness, to deal with a few limitations 
 * in JFX and TestFX
 */
class RunningAsTestEvent

/**
 * Generic message to be shown to user
 */
class UserMessageEvent(val message: String)

/**
 * Clear all user messages
 */
class ClearMessagesEvent()
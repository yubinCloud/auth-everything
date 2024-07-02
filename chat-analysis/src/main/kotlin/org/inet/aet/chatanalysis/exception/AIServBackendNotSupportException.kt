package org.inet.aet.chatanalysis.exception

/**
 * 表示当前使用的 AI Backend 不支持当前的功能
 */
class AIServBackendNotSupportException(message: String): BaseBzException(message)
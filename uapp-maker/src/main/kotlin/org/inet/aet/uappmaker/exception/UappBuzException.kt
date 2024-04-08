package org.inet.aet.uappmaker.exception


class UappNotFoundException(private val uappId: String): BaseBuzException("Uapp $uappId 不存在")

/**
 * Uapp （查看/编辑）权限异常
 */
class UappPermissionException (override val message: String): BaseBuzException(message)

/**
 * Uapp 类型与期望不同
 */
class UappTypeMismatchException(override val message: String): BaseBuzException(message)

/**
 * Uapp 操作错误
 */
class UappOprException(override val message: String): BaseBuzException(message)
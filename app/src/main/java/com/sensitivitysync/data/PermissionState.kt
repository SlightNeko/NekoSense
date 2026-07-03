package com.sensitivitysync.data

enum class PermissionState {
    NOT_REQUESTED,
    GRANTED,
    DENIED
}

data class Permissions(
    val overlay: PermissionState = PermissionState.NOT_REQUESTED,
    val shizuku: PermissionState = PermissionState.NOT_REQUESTED,
    val mediaProjection: PermissionState = PermissionState.NOT_REQUESTED,
    val notification: PermissionState = PermissionState.NOT_REQUESTED
) {
    val allGranted: Boolean
        get() = overlay == PermissionState.GRANTED &&
                shizuku == PermissionState.GRANTED &&
                mediaProjection == PermissionState.GRANTED &&
                notification == PermissionState.GRANTED
}

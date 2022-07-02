package me.nekocloud.auth.core;

public enum AuthAction {
	SUCCESS_COMPLETE_DEFAULT,
	SUCCESS_COMPLETE_WITH_2FA,

	WAIT_2FA_CODE,
	WAIT_COMPLETE,
	;
}

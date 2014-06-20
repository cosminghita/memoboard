package com.espresoh.memoboard.server.session;

import com.gncsoft.util.exception.BaseException;

public class MBException extends BaseException {

	
	// ==================== 1. Static Fields ========================

	private static final long serialVersionUID = 4247421653094918467L;

	
	// ==================== 4. Constructors ====================

	public MBException(String message, Exception e)
	{
		super(message, e);
	}

	

}

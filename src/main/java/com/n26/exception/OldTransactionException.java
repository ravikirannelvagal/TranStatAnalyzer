package com.n26.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NO_CONTENT, reason="Transaction is more than a minute old")
public class OldTransactionException extends RuntimeException{

	public OldTransactionException(){
		System.out.println("creating exception obj");
	}
}

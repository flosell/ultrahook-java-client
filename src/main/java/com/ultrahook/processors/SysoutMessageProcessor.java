package com.ultrahook.processors;

import com.ultrahook.UltrahookMessageProcessor;
import com.ultrahook.dtos.UltrahookMessage;

public class SysoutMessageProcessor implements UltrahookMessageProcessor{

	@Override
	public void process(UltrahookMessage msg) {
		System.out.println(msg);
	}

}

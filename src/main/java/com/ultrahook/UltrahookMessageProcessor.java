package com.ultrahook;

import com.ultrahook.dtos.UltrahookMessage;

public interface UltrahookMessageProcessor {
	public void process(UltrahookMessage msg);
}

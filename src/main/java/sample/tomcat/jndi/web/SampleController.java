/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.tomcat.jndi.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sample.tomcat.jndi.legacycode.UnmodifiableLegacyCode;
import sample.tomcat.jndi.services.SampleJmsService;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

@RestController
public class SampleController {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private SampleJmsService jmsService;

	@RequestMapping("/factoryBean")
	public String factoryBean() {
		return "DataSource retrieved from JNDI using JndiObjectFactoryBean: " + dataSource;
	}

	@RequestMapping("/direct")
	public String direct() throws NamingException {
		return "DataSource retrieved directly from JNDI: " +
				new InitialContext().lookup("java:comp/env/jdbc/myDataSource");
	}

	@RequestMapping("/queueFromJndi")
	public String queueFromJndi() throws NamingException {
		return "JMS Queue retrieved directly from JNDI by legacy code: " +
				UnmodifiableLegacyCode.getMessageQueue();
	}

	@RequestMapping("/message/{text}")
	public String sendMessage(@PathVariable String text) {
		jmsService.sendMessage(text);
		return "message [" + text + "] sent";
	}

}

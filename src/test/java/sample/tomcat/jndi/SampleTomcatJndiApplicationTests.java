/*
 * Copyright 2012-2015 the original author or authors.
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

package sample.tomcat.jndi;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SampleTomcatJndiApplication.class)
@DirtiesContext
public class SampleTomcatJndiApplicationTests {

	@Value("${local.server.port}")
	private int port;

	@Test
	public void testDirect() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/direct", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertThat(entity.getBody(), containsString(BasicDataSource.class.getName()));
	}

	@Test
	public void testFactoryBean() throws Exception {
		ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
				"http://localhost:" + this.port + "/factoryBean", String.class);
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertThat(entity.getBody(), containsString(BasicDataSource.class.getName()));
	}

}

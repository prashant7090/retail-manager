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

import java.util.Map;

import com.retailmanager.dao.ShopDao;
import com.retailmanager.impl.ShopDaoImpl;
import com.retailmanager.model.Shop;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.Assert.assertEquals;

/**
 * Basic integration tests for service demo application.
 *
 * @author Dave Syer
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = BootRetailManager.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class BootRetailManagerTests {

	@LocalServerPort
	private int port;

	@Value("${local.management.port}")
	private int mgt;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void shouldReturnBooleanAfterAddingFirstShop() throws Exception {
		ShopDao shopDao = new ShopDaoImpl();
		Shop shop = new Shop();
		shop.setShopAddress("ABC");
		shop.setShopName("ABCBooks");
		shop.setShopPostCode("411020");
		Map firstShop = shopDao.addShop(shop);

		assertEquals(firstShop.containsKey("shop"), true);
	}

}

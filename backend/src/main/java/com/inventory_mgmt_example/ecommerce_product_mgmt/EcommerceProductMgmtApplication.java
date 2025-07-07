package com.inventory_mgmt_example.ecommerce_product_mgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableCaching
@EnableMongoAuditing
public class EcommerceProductMgmtApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceProductMgmtApplication.class, args);
	}

}

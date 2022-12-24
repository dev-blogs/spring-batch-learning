package com.example.batch.writer;

import java.util.HashMap;
import java.util.List;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import com.example.model.Product;
import javax.sql.DataSource;

public class ProductJdbcItemWriter implements ItemWriter<Product> {
	public static final String INSERT_PRODUCT = "insert into products " + "(id,name,description,price) values(:id,:name,:description,:price)";
	public static final String UPDATE_PRODUCT = "update products set " + "name=:name, description=:description, price=:price where id=:id";

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ProductJdbcItemWriter(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Override
	public void write(List<? extends Product> items) throws Exception {
		for (Product item : items) {
			SqlParameterSource sqlParameterSource = new MapSqlParameterSource(new HashMap<String, Object>() {
				{
					put("id", item.getId());
					put("name", item.getName());
					put("description", item.getDescription());
					put("price", item.getPrice());
				}
			});

			int updated = namedParameterJdbcTemplate.update(UPDATE_PRODUCT, sqlParameterSource);
			if (updated == 0) {
				namedParameterJdbcTemplate.update(INSERT_PRODUCT, sqlParameterSource);
			}
		}
	}
}
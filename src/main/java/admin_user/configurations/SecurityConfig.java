package admin_user.configurations;

import admin_user.service.CustomSuccessHandler;
import admin_user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}
	@Autowired
	UserServiceImpl userService;

	@Autowired
	CustomSuccessHandler customSuccessHandler;
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(c -> c.disable())
				.authorizeHttpRequests(authz -> authz
						.requestMatchers( "/pages/dashboard", "/pages/categories", "pages/user-list","pages/StaffRegistration","pages/user-profile","pages/logout","/pages/subcategories", "/pages/brand",
								"/pages/addproduct", "/pages/addInventory", "/pages/viewproduct", "/pages/viewinventory","/pages/Create sales Oder",
								"/pages/View sales Order", "/pages/pos", "categories/add", "categories/category-list", "categories/listing",
								"subcategories/add", "/subcategories/listing", "subcategories/subcategory-list", "brand/list", "brand/brand-list",
								"brand/add", "/brand/listing", "products/list", "products/product-list", "products/add","products/view",
								"/getByProductCode", "/products/edit/{id}","/products/get-product-by-code/{product_code}", "/inventory/addInventory",
								"/products/listing", "/inventory/inventory-list", "/inventory/listing", "/inventory/addInventory", "/categories/edit/{id}",
								"css/datatable/*", "js/datatables/*", "/pos/create-new-order",  "/pos/get-pending-orders", "/pos/add-product-to-order",
								"/pos/get-order-items/{id}", "/pos/get-order-details/{id}", "/place-order", "/pages/invoice", "/pos/get-invoice-details/{order_id}").hasAuthority("ADMIN")
						.requestMatchers("vendors/*", "vendors/css/*", "vendors/js/*", "css/*", "images/*", "images/faces/*",
								"js/*", "images/samples/*", "vendors/iconfonts/mdi/css/*", "vendors/iconfonts/mdi/fonts/*", "/*",
								"/registration", "/forgot/**", "/resetemail", "/logout", "/login", "/pos/listing").permitAll()
						.anyRequest().authenticated())

				.formLogin(form -> form.loginPage("/login")
						.successHandler(customSuccessHandler)
						.permitAll());


		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

}

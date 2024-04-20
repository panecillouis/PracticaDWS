package com.godfathercapybara.capybara.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity(name="USERS")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {

    @Id
    private Long id;
	
	private String name;

	private String lastName;

	private String email;

    private String username;
    
    private String password;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    public User() {
	}

	public User(String username, String encodedPassword, String... roles) {
		this.username = username;
		this.password = encodedPassword;
		this.roles = List.of(roles);
	}
	public User(String username, String name, String email, String lastName, String encodedPassword, String... roles) {
		this.name = name;
		this.email = email;
		this.lastName = lastName;
		this.username = username;
		this.password = encodedPassword;
		this.roles = List.of(roles);
	}
	public void setId(long id)
	{
		this.id=id;
	
	}
	public long getId()
	{
		return this.id;
	}
	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLastName() {
		return this.lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEmail() {
		return this.email;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

}

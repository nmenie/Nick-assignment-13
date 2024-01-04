package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/register")
	public String getCreateUser (ModelMap model) {
		
		model.put("user", new User());
		
		return "register";
	}
	
	@PostMapping("/register")
	public String postCreateUser (User user) {
		System.out.println(user);
		userService.saveUser(user);
		return "redirect:/register";
	}
	
	@GetMapping("/users")
	public String getAllUsers (ModelMap model) {
		Set<User> users = userService.findAll();
		
		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}
		
		return "users";
	}
	
	@GetMapping("/users/{userId}")
	public String getOneUser (ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		System.out.println(user.getAccounts());
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		return "users";
	}
	
	@PostMapping("/users/{userId}")
	public String postOneUser (User user) {
		userService.saveUser(user);
		return "redirect:/users/"+user.getUserId();
	}
	
	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser (@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}
	
	

	 @GetMapping("/users/{userId}/accounts")
	    public String getUserAccounts(ModelMap model, @PathVariable Long userId) {
	        User user = userService.findById(userId);

	        if (user.getAccounts().isEmpty()) {
	            // If there are no accounts, create a default one
	            Account defaultAccount = new Account();
	            defaultAccount.setAccountName("Account #1");
	            user.getAccounts().add(defaultAccount);
	            userService.saveUser(user);
	        }

	        model.put("user", user);
	        model.put("accounts", user.getAccounts());
	        return "accounts";
	    }

	 @PostMapping("/users/{userId}/accounts")
	 public String createNewAccount(@PathVariable Long userId, ModelMap model) {
	     User user = userService.findById(userId);

	     // Create a new account with the next account number
	     int nextAccountNumber = user.getAccounts().size() + 1;
	     Account newAccount = new Account();
	     newAccount.setAccountName("Account #" + nextAccountNumber);

	     // Save the new account
	     userService.saveAccount(newAccount);  // Save the account first

	     // Associate the account with the user
	     newAccount.getUsers().add(user);

	     // Save the user (including the association with the new account)
	     userService.saveUser(user);

	     // Update the model
	     model.put("user", user);
	     model.put("accounts", user.getAccounts());
	     return "accounts";
	 }


	    @PostMapping("/users/{userId}/accounts/{accountId}")
	    public String updateAccountName(
	            @PathVariable Long userId,
	            @PathVariable Long accountId,
	            @RequestParam String accountName,
	            ModelMap model) {
	        User user = userService.findById(userId);

	        // Find the account to update
	        Optional<Account> optionalAccount = user.getAccounts().stream()
	                .filter(account -> account.getAccountId().equals(accountId))
	                .findFirst();

	        if (optionalAccount.isPresent()) {
	            // Update the account name
	            Account account = optionalAccount.get();
	            account.setAccountName(accountName);
	            userService.saveUser(user);
	        }

	        // Update the model
	        model.put("user", user);
	        model.put("accounts", user.getAccounts());
	        return "accounts";
	    }
	

	}


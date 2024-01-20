package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.Address;
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
		System.out.println(users);
		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}
		
		return "users";
	}
	
	@PostMapping("/users")
	public String updateOneUser (User user) { 
		System.out.println(user);
		 User foundUser = userService.findById(user.getUserId());
		    Address userAddress = user.getAddress();
		    System.out.println(user);
		    userAddress.setUser(foundUser);
		    userAddress.setUserId(user.getUserId());
		    foundUser.setName(user.getName());
		    foundUser.setPassword(user.getPassword());
		    foundUser.setUsername(user.getUsername());
		    foundUser.setAddress(userAddress);
		    
		    userService.saveUser(foundUser);

		    return "redirect:/users/" + user.getUserId();
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
	public String postOneUser(User user) {
	    User foundUser = userService.findById(user.getUserId());
	    Address userAddress = user.getAddress();
	    System.out.println(user);
	    userAddress.setUser(foundUser);
	    userAddress.setUserId(user.getUserId());
	    foundUser.setName(user.getName());
	    foundUser.setPassword(user.getPassword());
	    foundUser.setUsername(user.getUsername());
	    foundUser.setAddress(userAddress);
	    
	    userService.saveUser(foundUser);

	    return "redirect:/users/" + user.getUserId();
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
				model.put("account", defaultAccount);
	            userService.saveUser(user);
	        }

			Account newAccount = new Account();
			newAccount.setAccountName("Account #" + (user.getAccounts().size() + 1));
			newAccount.getUsers().add(user);
	        model.put("user", user);
	        model.put("accounts", user.getAccounts());
			model.put("account", newAccount);
		 	newAccount.getUsers().stream().forEach(System.out::println);
	        return "accounts";
	    }

	 @PostMapping("/users/{userId}/accounts")
	 public String createNewAccount(@PathVariable Long userId, ModelMap model, Account account) {
	     User user = userService.findById(userId);

	     // Create a new account

//		 account.setAccountName("Account #" + (user.getAccounts().size() + 1));

	     // Associate the account with the user
		 account.getUsers().add(user);
		 user.getAccounts().add(account);

	     // Save the new account (including the association with the user)
	     userService.saveAccount(account);
			userService.saveUser(user);
	     // Update the model
	     model.put("user", user);
	     model.put("accounts", user.getAccounts());
		 model.put("account", account);
	     return "accounts";
	 }



	    @PostMapping("/users/{userId}/accounts/{accountId}")
	    public String updateAccountName(
	            @PathVariable Long userId,
	            @PathVariable Long accountId,
	            @RequestParam String accountName,
	            ModelMap model,
				Account accountOnMap) {
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
			model.put("account", optionalAccount.get());
	        return "accounts";
	    }
	    
	    @GetMapping("/users/{userId}/accounts/{accountId}")
	    public String getAccountDetails(
	            @PathVariable Long userId,
	            @PathVariable Long accountId,
	            ModelMap model) {
	        User user = userService.findById(userId);

	        // Find the account to display
	        Optional<Account> optionalAccount = user.getAccounts().stream()
	                .filter(account -> account.getAccountId().equals(accountId))
	                .findFirst();

			System.out.println("OPTIONALLLLL: " + optionalAccount.get().getAccountName());
	        if (optionalAccount.isPresent()) {
	            // Add the account details to the model
	            model.put("user", user);
	            model.put("account", optionalAccount.get());
	            return "account-details"; // Replace with your actual Thymeleaf template
	        } else {
	            // Handle account not found, e.g., redirect to an error page
	            return "redirect:/error";
	        }
	    }


	

	}


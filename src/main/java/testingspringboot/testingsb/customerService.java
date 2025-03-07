package testingspringboot.testingsb;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class customerService {

    @Autowired
    private customerRepository customerRepository;

    public List<customerinfo> getAllCustomers() {
        return customerRepository.findAll();
    }


    public List<customerinfo> getCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public Optional<customerinfo> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public Optional<customerinfo> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public customerinfo saveCustomerinfo(customerinfo customer) {
        if (!customerRepository.existsByEmail(customer.getEmail())) {
            return customerRepository.save(customer);
        } else {
            throw new RuntimeException("Duplicate customer entry: " + customer.getEmail());
        }
    }
    public customerinfo updateCustomer(Long id, customerinfo updatedCustomer) {
        Optional<customerinfo> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            customerinfo existingCustomer = optionalCustomer.get();
            if (!customerRepository.existsByEmail(updatedCustomer.getEmail()) || 
                existingCustomer.getEmail().equals(updatedCustomer.getEmail())) {
                existingCustomer.setName(updatedCustomer.getName());
                existingCustomer.setEmail(updatedCustomer.getEmail());
                return customerRepository.save(existingCustomer);
            } else {
                throw new RuntimeException("Duplicate email entry: " + updatedCustomer.getEmail());
            }
        } else {
            throw new RuntimeException("Customer not found");
        }
    }
    
    public boolean addCustomer(String name, String email) {
        if (!customerRepository.existsByEmail(email)) {
            customerinfo customer = new customerinfo();
            customer.setName(name);
            customer.setEmail(email);
            customerRepository.save(customer);
            return true;
        } else {
            return false;
        }
    }
    
    public void deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
        } else {
            throw new RuntimeException("Customer was not found");
        }
    }
}
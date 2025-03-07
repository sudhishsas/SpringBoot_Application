package testingspringboot.testingsb;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class CsvService {

    @Autowired
    private customerRepository customerRepository;

    public List<String[]> readCsv(String fileName, String directory) throws CsvValidationException {
        List<String[]> data = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(directory + "/" + fileName))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (!customerRepository.existsByEmail(line[1])) {
                    customerinfo customer = new customerinfo();
                    customer.setName(line[0]);
                    customer.setEmail(line[1]);
                    customerRepository.save(customer);
                    data.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("THE ERROR ON THE DATABASE SIDE."+ e.getMessage());
        }

        return data;
    }

    // public String addOne(customerinfo customer) throws IOException{
    //     customerRepository.save(customer);
    //     return "Added" + customer;
    // }

    public List<customerinfo> getAllCustomers() {
        
        return customerRepository.findAll();

    }

}
    

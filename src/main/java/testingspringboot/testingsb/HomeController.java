package testingspringboot.testingsb;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.opencsv.exceptions.CsvValidationException;

// import io.micrometer.core.ipc.http.HttpSender;



@Controller
@SpringBootApplication(scanBasePackages = {"testingspringboot.testingsb"})
public class HomeController {

        @Autowired
        private CsvService csvService;

        @Autowired
        private customerService customerService;

        @Value("${upload.path}")
        private String uplaodPath;

        @RequestMapping("/hello")
        @ResponseBody
        public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
            return String.format("Hello %s!", name);
        }

        @GetMapping("/")
        public String landing(Model model) {
            model.addAttribute("title", "Welcome to the Landing Page. ");
            model.addAttribute("message", "This is my first Spring web site with a dynamically generated message.");
            return "landing"; 
        }

        @GetMapping("/Addnewcustomer")
        public String addnew(Model model) {
            return "Addcustomer";
        }

        @PostMapping("/Addcustomer/added")
        public String addCustomer(@RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  Model model) {
            List<String> messages = new ArrayList<>();
            if (customerService.addCustomer(name, email)) {
                messages.add("Customer added successfully!");
            } else {
                messages.add("Failed to add customer: Duplicate entry for email: " + email);
            }
            model.addAttribute("message", messages);
            return "Addcustomer";
        }

        @GetMapping("/Addcustomers/added/{filename}")
        public String addCustomers(@PathVariable("filename") String filename,
                                  Model model) throws CsvValidationException {
            List<String> message = new ArrayList<>();
            String filepath  = uplaodPath+ File.separator + filename;
            // Creating a new File instance
            File file = new File(filepath);
        
            if (file.exists()) {
                List<String[]> result = csvService.readCsv(filename, uplaodPath);
                if (!result.isEmpty()) {
                    message.add("Added customers successfully: " + filename);
                } else {
                    message.add("Failed to add customers: " + filename);
                }
            } else {
                message.add("File not found: " + filename);
            }
            model.addAttribute("message", message);

            return getFiles(model);
        }

        

        @GetMapping("/files/new")
        public String newFile(Model model) {
            return "upload";
        }
        
        @PostMapping("/files/upload")
        public String uploadFiles(Model model, @RequestParam("files") MultipartFile[] files) {
            List<String> messages = new ArrayList<>();
            
            Arrays.asList(files).stream().forEach(file -> {
            try {
                //storageService.save(file);
                String filePath = uplaodPath + File.separator + file.getOriginalFilename();
                try ( // Creating an object of FileOutputStream class
                        FileOutputStream fout = new FileOutputStream(filePath)) {
                    fout.write(file.getBytes());
                    
                }
                messages.add(file.getOriginalFilename() + " [file uploaded Successfully]");
                List<String[]> data = csvService.readCsv(file.getOriginalFilename(), uplaodPath);

            } catch (Exception e) {
                messages.add(file.getOriginalFilename() + " <Failed> - " + e.getMessage());
            }
            });

            model.addAttribute("messages", messages);

            return "upload";
        }
        
        public List<String[]> readCsv( String fileName, String directory) throws CsvValidationException {
            return csvService.readCsv(fileName, directory);
        }
    
        @GetMapping("/viewcustomers")
        public String viewCustomers(Model model) {
            List<customerinfo> customers = customerService.getAllCustomers();
            List<String> messages = new ArrayList<>();

            if (customers.isEmpty()) {
                messages.add("No customers to retrieve.");
            } else {
                model.addAttribute("customers", customers);
            }
            model.addAttribute("messages",messages);
            return "viewcustomers";
        }
        // @GetMapping("/uploadStatus")
        // public String uploadStatus(ModelMap m) {
        // return "landing";
        // }
        @GetMapping("/getfiles")
        public String getFiles(Model model) {
            List<String> messages = new ArrayList<>();
            // Creating a new File instance
            File directory = new File(uplaodPath);

            // Checking if the directory exists and is a directory
            if (!directory.exists() || !directory.isDirectory()) {
                messages.add("The upload directory does not exist.");
                model.addAttribute("messages", messages);
                return "listfiles";  // Return the view name for the file list
            }
        
            // list() method returns an array of strings naming the files and directories
            // in the directory denoted by this abstract pathname
            String[] filenames = directory.list();
        
            // Checking if filenames is null or empty
            if (filenames == null || filenames.length == 0) {
                messages.add("No files found in the directory.");
                model.addAttribute("messages", messages);
                model.addAttribute("files", Arrays.asList());
                return "listfiles";  // Return the view name for the file list
            }
        
            // Converting array to list
            List<String> fileList = Arrays.asList(filenames);
            model.addAttribute("messages", messages);
            model.addAttribute("files", fileList);
            return "listfiles";  // Return the view name for the file list
        }
        
        public List<String> getFileslist()  {
            String folderPath = uplaodPath;
    
        // Creating a new File instance
        File directory = new File(folderPath);

        // list() method returns an array of strings naming the files and directories
        // in the directory denoted by this abstract pathname
        String[] filenames = directory.list();

        // Converting array to list
        List<String> fileList = Arrays.asList(filenames);
            return fileList;  // Return the view name for the file list
        }
      
        @GetMapping("file/delete/{filename}")
        public String removeFile(@PathVariable("filename") String filename, Model model)  {
            List<String> message = new ArrayList<>();
            String filepath  = uplaodPath+ File.separator + filename;
            // Creating a new File instance
            File file = new File(filepath);
        
            if (file.exists()) {
                if (file.delete()) {
                    message.add("File deleted successfully: " + filename);
                } else {
                    message.add("Failed to delete file: " + filename);
                }
            } else {
                message.add("File not found: " + filename);
            }
            model.addAttribute("message", message);

            return getFiles(model);  // Return the view name for the file list
        }
        
    // // Downloading a file
    // @GetMapping("/download/{path:.+}")
    // public ResponseEntity<InputStreamResource> downloadFile(@PathVariable("path") String filename) throws FileNotFoundException {
    //     // Checking whether the file requested for download exists or not
    //     String fileUploadpath = uplaodPath + "/Uploads";
    //     List<String> filenames = this.getFiles().getBody();
    //     boolean contains =filenames.contains(filename);
    //     if (!contains) {
    //         return new ResponseEntity("File Not Found", HttpStatus.NOT_FOUND);
    //     }
    //     // Setting up the filepath
    //     String filePath = fileUploadpath + File.separator + filename;
    //     // Creating new file instance
    //     File file = new File(filePath);
    //     // Creating a new InputStreamResource object
    //     InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
    //     // Creating a new instance of HttpHeaders Object
    //     HttpHeaders headers = new HttpHeaders();
    //     // Setting up values for contentType and headerValue
    //     String contentType = "application/octet-stream";
    //     String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
    //     return (ResponseEntity<InputStreamResource>) ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, headerValue);  
    //     }
        @GetMapping("/customer/search")
        public String searchCustomer(@RequestParam("searchType") String searchType,
                                    @RequestParam("query") String query,
                                    Model model) {
            List<String> messages = new ArrayList<>();
            List<customerinfo> customers = new ArrayList<>();

            if ("name".equalsIgnoreCase(searchType)) {
                customers = customerService.getCustomersByName(query);
                if (!customers.isEmpty()) {
                    model.addAttribute("customers", customers);
                } else {
                    messages.add("Customer not found");
                }
            } else if ("id".equalsIgnoreCase(searchType)) {
                try {
                    Long id = Long.valueOf(query);
                    Optional<customerinfo> optionalCustomer = customerService.getCustomerById(id);
                    if (optionalCustomer.isPresent()) {
                        customers.add(optionalCustomer.get());
                    } else {
                        messages.add("Customer not found");
                    }
                } catch (NumberFormatException e) {
                    messages.add("Invalid ID format");
                }
            }
            model.addAttribute("customers", customers);
            model.addAttribute("messages", messages);
            return "search";
        }
    
    
        @GetMapping("/customer")
        @ResponseBody
        public customerinfo getCustomerByName(@RequestParam("name") String name) {
            Optional<customerinfo> optionalCustomer = customerService.getCustomerByName(name);
            return optionalCustomer.orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        
        @GetMapping("/customer/update/{id}")
        public String showUpdateForm(@PathVariable("id") Long id, Model model) {
            Optional<customerinfo> optionalCustomer = customerService.getCustomerById(id);
            if (optionalCustomer.isPresent()) {
                model.addAttribute("customer", optionalCustomer.get());
                return "update";
            } else {
                throw new RuntimeException("Customer not found");
            }
        }
      
        @PostMapping("/customer/update/{id}")
        public String updateCustomer(@PathVariable("id") Long id,
                                     @ModelAttribute("customer") customerinfo updatedCustomer,
                                     @RequestParam("action") String action, Model model) {
            List<String> messages = new ArrayList<>();
            if ("submit".equals(action)) {
                try {
                    customerService.updateCustomer(id, updatedCustomer);
                    messages.add("Customer updated successfully!");
                } catch (RuntimeException e) {
                    messages.add(e.getMessage());
                    model.addAttribute("customer", updatedCustomer);
                    model.addAttribute("messages", messages);
                    return "update";
                }
                model.addAttribute("messages", messages);
                return "redirect:/viewcustomers";
            } else if ("cancel".equals(action)) {
                return "redirect:/viewcustomers";
            }
            return "update";
        }
        

        @GetMapping("/customer/delete/{id}")
        public String deleteCustomer(@PathVariable("id") Long id, Model model) {
            List<String> messages = new ArrayList<>();
            try {
                customerService.deleteCustomer(id);
                messages.add("Customer deleted successfully!");
            } catch (RuntimeException e) {
                messages.add( "Error: " + e.getMessage());
            }
            model.addAttribute("messages", messages);
            return "redirect:/viewcustomers";
        }
        

        public static void main(String[] args) {
 		SpringApplication.run(HomeController.class, args);
 	    }



}
        

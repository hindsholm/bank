package dk.hindsholm.bank.boundary;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AccountUpdate {

    @Pattern(regexp = "^\\d{4}$")
    private String regNo;

    @Pattern(regexp = "^\\d+$")
    private String accountNo;

    @NotNull
    private String name;

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

}

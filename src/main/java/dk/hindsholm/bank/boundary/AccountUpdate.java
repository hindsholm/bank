package dk.hindsholm.bank.boundary;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AccountUpdate {

    @Pattern(regexp = "^[0-9]{4}$")
    private String regNo;

    @Pattern(regexp = "^[0-9]+$")
    private String accountNo;

    @NotNull
    @Pattern(regexp = ".{1,40}")
    private String name;

    public String getRegNo() {
        return regNo;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getName() {
        return name;
    }
}

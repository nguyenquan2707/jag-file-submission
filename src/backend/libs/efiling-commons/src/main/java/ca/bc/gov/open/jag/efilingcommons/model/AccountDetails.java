package ca.bc.gov.open.jag.efilingcommons.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AccountDetails {

    private BigDecimal accountId;
    private BigDecimal clientId;
    private String firstName;
    private String lastName;
    private String email;
    private String middleName;
    private boolean efileRole;

    protected AccountDetails(AccountDetails.Builder builder) {


        this.accountId = builder.accountId;
        this.clientId = builder.clientId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.middleName = builder.middleName;
        this.efileRole = builder.efileRole;

    }

    public static AccountDetails.Builder builder() {

        return new AccountDetails.Builder();
    }

    @JsonCreator
    public AccountDetails(@JsonProperty("accountId") BigDecimal accountId,
                                        @JsonProperty("clientId") BigDecimal clientId,
                                        @JsonProperty("efileRole") boolean efileRole,
                                        @JsonProperty("firstName") String firstName,
                                        @JsonProperty("lastName") String lastName,
                                        @JsonProperty("middleName") String middleName,
                                        @JsonProperty("email") String email) {
        this.accountId = accountId;
        this.clientId = clientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.middleName = middleName;
        this.efileRole = efileRole;
    }

    public BigDecimal getAccountId() {
        return accountId;
    }

    public BigDecimal getClientId() {
        return clientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMiddleName() {
        return middleName;
    }

    public boolean isEfileRole() {
        return efileRole;
    }

    public static class Builder {

        private BigDecimal accountId;
        private BigDecimal clientId;
        private String firstName;
        private String lastName;
        private String email;
        private String middleName;
        private boolean efileRole;

        public Builder accountId(BigDecimal accountId) {
            this.accountId = accountId;
            return this;
        }

        public Builder clientId(BigDecimal clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder middleName(String middleName) {
            this.middleName = middleName;
            return this;
        }

        public Builder efileRole(boolean efileRole) {
            this.efileRole = efileRole;
            return this;
        }

        public AccountDetails create() {
            return new AccountDetails(this);
        }
    }

}

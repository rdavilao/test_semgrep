package au.com.projects.tender.service;

import au.com.projects.tender.api.dto.AccountRQ;
import au.com.projects.tender.api.dto.ChangeAccountInfo;
import au.com.projects.tender.exception.DocumentNotFoundException;
import au.com.projects.tender.exception.InsertException;
import au.com.projects.tender.exception.LoginException;
import au.com.projects.tender.model.Account;
import au.com.projects.tender.model.Bid;
import au.com.projects.tender.repository.AccountRepository;
import au.com.projects.tender.repository.BidRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final BidRepository bidRepository;
    private String errorMsg;

    public AccountService(AccountRepository accountRepository, BidRepository bidRepository) {
        this.accountRepository = accountRepository;
        this.bidRepository = bidRepository;
    }

    public void create(Account account) throws InsertException {
        try {
            log.info("Creating account: " + account.getEmail());
            Account acc = this.accountRepository.findByEmail(account.getEmail());
            if (acc == null) {
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[16];
                random.nextBytes(salt);
                account.setSalt(salt);
                account.setPwd(hashStringAndSalt(account.getEmail(), salt));
                account.setEmailHashed(hashStringAndSalt(account.getEmail(), salt));
                account.setCreationDate(new Date(Instant.now().toEpochMilli()));
                account.setEnabled(true);
                this.accountRepository.save(account);
            } else {
                this.errorMsg = "User already registered !";
                log.error(this.errorMsg);
                throw new InsertException(Account.class.getSimpleName(), this.errorMsg);
            }
        } catch (Exception e) {
            log.error("Error creating new account: " + e);
            throw new InsertException(Account.class.getSimpleName(), Objects.requireNonNullElse(this.errorMsg, "Error creating new account"));
        }
    }

    public void update(ChangeAccountInfo changeAccountInfo) throws Exception {
        try {
            log.info("Updating account info");
            Account account = this.accountRepository.findByEmailHashed(changeAccountInfo.getEmailHashed());
            if (account == null) handleNullAccounts("Account doesn't exist.");
            assert account != null;
            log.info("Account: " + account.getEmail());

            account.setPhone(account.getPhone().equals(changeAccountInfo.getPhone()) ? account.getPhone() : changeAccountInfo.getPhone());
            account.setEnabled(account.getEnabled().equals(changeAccountInfo.getEnabled()) ? account.getEnabled() : changeAccountInfo.getEnabled());
            this.accountRepository.save(account);

        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error updating user information!":this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void delete(String emailHashed) throws Exception {
        try {
            log.info("Deleting account");
            Account account = this.accountRepository.findByEmailHashed(emailHashed);
            if (account == null) handleNullAccounts("Account doesn't exist.");
            assert account != null;
            log.info("Account: " + account.getEmail());

            if(account.getRole().equals("admin")) {
                Integer amountAdminAccounts = this.accountRepository.countByRole("admin");
                if(amountAdminAccounts.equals(1)) handleMinimumAmountOfAdminAccounts();
            }

            List<Bid> bids = this.bidRepository.findByAccountId(account.getId());

            if (!bids.isEmpty())
                handleBidsSubmitted("The account cannot be deleted as it has registered bids; the system's accountability and transparency do not allow it. " +
                        "\nOnly user deactivation is possible.");

            this.accountRepository.delete(account);

        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error deleting account!":this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public void resetAccountPwd(String emailHashed) throws Exception {
        try {
            log.info("Resetting account password");
            Account account = this.accountRepository.findByEmailHashed(emailHashed);
            if (account == null) handleNullAccounts("Account doesn't exist.");
            assert account != null;
            log.info("Account: " + account.getEmail());

            account.setPwd(hashStringAndSalt(account.getEmail(), account.getSalt()));
            this.accountRepository.save(account);

        } catch (Exception ex) {
            this.errorMsg = this.errorMsg == null ? "Error resetting account password!":this.errorMsg;
            log.error(this.errorMsg);
            throw new Exception(this.errorMsg);
        }
    }

    public AccountRQ login(String email, String pwd) throws LoginException {
        try {
            log.info("Login, email: " + email);
            Account account = this.accountRepository.findByEmail(email);
            if (account != null) {
                String hashedPwd = hashStringAndSalt(pwd, account.getSalt());
                if (hashedPwd.equals(account.getPwd())) {
                    log.info("Login successful");
                    account.setLastLogin(new Date(Instant.now().toEpochMilli()));
                    this.accountRepository.save(account);
                    if (!account.getEnabled()) handleNotEnabledAccount();
                    AccountRQ accountRQ = new AccountRQ();
                    accountRQ.setName(account.getEmail());
                    accountRQ.setEmail(account.getEmailHashed());
                    Account accountBaseSalt = this.accountRepository.findByEmail("admin@tender.sellmycarwa.au");
                    accountRQ.setRole(hashStringAndSalt(account.getRole(), accountBaseSalt.getSalt()));
                    return accountRQ;
                } else {
                    this.errorMsg = "Incorrect username or password!";
                    log.error(this.errorMsg);
                    throw new LoginException(Account.class.getSimpleName(), this.errorMsg);
                }
            } else {
                this.errorMsg = "Incorrect username or password!";
                log.error(this.errorMsg);
                throw new LoginException(Account.class.getSimpleName(), this.errorMsg);
            }
        } catch (Exception e) {
            log.error("Login failed: " + e);
            throw new LoginException(Account.class.getSimpleName(), Objects.requireNonNullElse(this.errorMsg, "Error while logging in!"));
        }
    }

    public List<Account> getAccountsByEmailContaining(String email) throws DocumentNotFoundException {
        try {
            List<Account> accountList = this.accountRepository.findByEmailContaining(email);

            if (accountList == null || accountList.isEmpty())
                handleNullAccounts("Error there is no account or accounts, with mail similar to " + email);

            return accountList;
        } catch (Exception e) {
            log.error("Error, getting accounts by email like: " + e);
            throw new DocumentNotFoundException(this.errorMsg != null ? this.errorMsg : "Error, getting accounts by email like.");
        }
    }

    public boolean hasToChangePwd(String emailHashed) throws DocumentNotFoundException {
        try {
            log.info("Searching if account need to change password.");
            Account account = this.accountRepository.findByEmailHashed(emailHashed);
            if (account == null) handleNullAccounts("Account doesn't exist.");
            assert account != null;

            String hashedDefaultPwd = hashStringAndSalt(account.getEmail(), account.getSalt());
            return account.getPwd().equals(hashedDefaultPwd);

        } catch (Exception e) {
            log.error("Error checking if the user should change the password: " + e);
            throw new DocumentNotFoundException(this.errorMsg != null ? this.errorMsg : "Error checking if the user should change the password.");
        }
    }

    public void changeAccountPassword(String emailHashed, String currentPwdGiven, String newPwd, String newPwdConfirmation) throws DocumentNotFoundException {
        try {
            log.info("Request to change password.");
            Account account = this.accountRepository.findByEmailHashed(emailHashed);
            if (account == null) handleNullAccounts("Account doesn't exist.");
            assert account != null;

            String currentPwdGivenHashed = hashStringAndSalt(currentPwdGiven, account.getSalt());

            if (currentPwdGivenHashed.equals(account.getPwd())) {
                if (passwordHasAtLeastOneNumberAndOneCapitalLetterAnd8Characteres(currentPwdGiven, newPwd)) {
                    if (newPwd.equals(newPwdConfirmation)) {
                        String newPwdHashed = hashStringAndSalt(newPwd, account.getSalt());
                        account.setPwd(newPwdHashed);
                        log.info("Password change successful.");
                        this.accountRepository.save(account);
                    } else {
                        this.errorMsg = "New password and new confirmation password are different.";
                        log.error(this.errorMsg);
                        throw new DocumentNotFoundException(this.errorMsg);
                    }
                } else {
                    this.errorMsg = "The new password does not meet the required parameters.";
                    log.error(this.errorMsg);
                    throw new DocumentNotFoundException(this.errorMsg);
                }
            } else {
                this.errorMsg = "Current password wrong.";
                log.error(this.errorMsg);
                throw new DocumentNotFoundException(this.errorMsg);
            }
        } catch (Exception e) {
            log.error("Error changing password: " + e);
            throw new DocumentNotFoundException(this.errorMsg != null ? this.errorMsg : "Error changing password.");
        }
    }

    private boolean passwordHasAtLeastOneNumberAndOneCapitalLetterAnd8Characteres(String currentPwd, String newPwd) {
        String atLeastOneNumberAndOneCapitalLetterPattern = "^(?=.*[A-Z])(?=.*\\d).+$";
        Pattern pattern = Pattern.compile(atLeastOneNumberAndOneCapitalLetterPattern);
        Matcher matcher = pattern.matcher(newPwd);

        return !currentPwd.equals(newPwd) && newPwd.length() >= 8 && matcher.matches();
    }


    private void handleNullAccounts(String msg) {
        log.error(msg);
        this.errorMsg = msg;
        try {
            throw new DocumentNotFoundException(this.errorMsg);
        } catch (DocumentNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleBidsSubmitted(String msg) {
        log.error(msg);
        this.errorMsg = msg;
        try {
            throw new Exception(this.errorMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMinimumAmountOfAdminAccounts() {
        log.error("The account cannot be deleted as there must always be at least one administrator-type account!");
        this.errorMsg = "The account cannot be deleted as there must always be at least one administrator-type account!";
        try {
            throw new Exception(this.errorMsg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleNotEnabledAccount() {
        log.error("Error, account is not enabled.");
        this.errorMsg = "Error, account is not enabled.";
        try {
            throw new LoginException(Account.class.getSimpleName(), this.errorMsg);
        } catch (LoginException e) {
            throw new RuntimeException(e);
        }
    }

    private String hashStringAndSalt(String value, byte[] salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeySpec spec = new PBEKeySpec(value.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

}

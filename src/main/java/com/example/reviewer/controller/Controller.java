package com.example.reviewer.controller;

import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeReportRepository;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EmployeeReviewRepository;
import com.example.reviewer.repository.EntityReportRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import com.example.reviewer.repository.FeedbackRepository;
import com.example.reviewer.repository.ReviewReportRepository;
import com.example.reviewer.repository.RoleDocumentRepository;
import com.example.reviewer.repository.SettingRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

/**
 * Base for all controllers. Contains all repositories to have access to database.
 * Also contains constants which are using through project.
 */
public class Controller {
    /**
     * Constant for pagination on rating page.
     */
    @Deprecated
    protected static final int AMOUNT_OF_ENTITIES_PER_PAGE = 10;

    /**
     * Amount of reports is required to block employee automatically.
     */
    protected static final int AMOUNT_OF_EMPLOYEE_REPORTS_TO_HIDE = 5;

    /**
     * Amount of reports is required to block entity automatically.
     */
    protected static final int AMOUNT_OF_ENTITY_REPORTS_TO_HIDE = 5;

    /**
     * Amount of review reports is required to block review automatically.
     */
    protected static final int AMOUNT_OF_REVIEW_REPORTS_TO_HIDE = 5;

    /**
     * Array of available data types for user documents.
     */
    protected static final String[] CONTENT_TYPES = {"image/jpg", "image/png", "image/jpeg"};

    /**
     * Path of folder where employees` photos are storing.
     */
    protected static final String EMPLOYEES_PATH = "data/employees/";

    /**
     * Path of folder where entities` photos are storing.
     */
    protected static final String ENTITIES_PATH = "data/entities/";

    /**
     * Max length of review text. It is using in database` columns length.
     */
    protected static final int MAX_REVIEW_TEXT_LENGTH = 1024;

    /**
     * Max size of document that user can upload.
     */
    protected static final Long MAX_UPLOAD_SIZE = 8 * 1024 * 1024L;

    /**
     * Max length of feedback text. It is using in database` columns length.
     */
    protected static final int MAX_FEEDBACK_LENGTH = 1024;

    /**
     * Max amount of reviews is available for user to left per one entity.
     */
    protected static final int MAX_REVIEW_PER_ENTITY = 3;

    /**
     * Rating user get for review when it is uploaded.
     */
    protected static final int RATING_FOR_LEFT_REVIEW = 1;

    /**
     * Rating user get for creation of entity.
     */
    protected static final int RATING_FOR_CREATION_ENTITY = 10;

    /**
     * Rating user get for creation of employee.
     */
    protected static final int RATING_FOR_CREATION_EMPLOYEE = 5;

    /**
     * Path of folder where user`s documents are storing.
     */
    protected static final String UPLOAD_PATH = "data/users/";

    @Autowired
    protected EmployeeReportRepository employeeReportRepository;

    @Autowired
    protected EntityReportRepository entityReportRepository;

    @Autowired
    protected EntityReviewRepository entityReviewRepository;

    @Autowired
    protected EmployeeReviewRepository employeeReviewRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected EntityRepository entityRepository;

    @Autowired
    protected FeedbackRepository feedbackRepository;

    @Autowired
    protected ReviewReportRepository reviewReportRepository;

    @Autowired
    protected RoleDocumentRepository roleDocumentRepository;

    @Autowired
    protected SettingRepository settingRepository;

    @Autowired
    protected UserRepository userRepository;

    /**
     * @return current user storing in session.
     */
    @ModelAttribute("user")
    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    /**
     * @return amount of entities storing in database.
     */
    @ModelAttribute("entityAmount")
    public long getEntityAmount() {
        return entityRepository.count();
    }

    /**
     * @return amount of employees storing in database.
     */
    @ModelAttribute("employeeAmount")
    public long getEmployeeAmount() {
        return employeeRepository.count();
    }

    /**
     * @return amount of entities reviews storing in database.
     */
    @ModelAttribute("entityReviewAmount")
    public long getEntityReviewAmount() {
        return entityReviewRepository.count();
    }

    /**
     * @return amount of employees reviews storing in database.
     */
    @ModelAttribute("employeeReviewAmount")
    public long getEmployeeReviewAmount() {
        return employeeReviewRepository.count();
    }
}

# Test Plan: ERP System

## Overview
This test plan covers the comprehensive test suite for the ERP (Educational Resource Planning) System. The system manages authentication, student enrollment, course management, section administration, and grade tracking across three primary user roles: Admin, Student, and Instructor.

## Test Strategy

### Test Approach
- **In-Memory Tests**: Core business logic validation using fake/mock DAOs (no database dependencies)
- **Database Integration Tests**: Critical data persistence scenarios with automatic cleanup
- **Service-Layer Tests**: Acceptance criteria validation across user workflows

### Test Architecture
1. **Fake DAOs** (`testutil/Fakes.java`): In-memory implementations of all data access objects
   - FakeSettingDao, FakeAuthDao, FakeCourseDao, FakeSectionDao, FakeEnrollmentDao, FakeGradeDao, FakeStudentDao, FakeInstructorDao
   - All tests using these run entirely in memory with no external I/O or database connections
   
2. **Database Integration Tests** (`data/CourseDaoTest.java`): 
   - Direct MySQL testing with explicit cleanup before (@BeforeEach) and after (@AfterEach) test execution
   - Ensures no persistent state changes in the database
   
3. **Service Tests** (`service/`): Business logic validation
   - AuthServiceTest, AdminServiceTest, StudentServiceTest, InstructorServiceTest
   - Uses fake DAOs to test service layer acceptance criteria

---

## Test Coverage by Module

### 1. Authentication & Access Control

#### 1.1 User Login (AuthServiceTest)
**Test**: `testLockAfterFailures`
- **Purpose**: Verify account lockout after failed login attempts
- **Steps**: 
  1. Attempt login 5 times with incorrect password
  2. Verify login is blocked with "Account locked" error
  3. Verify correct password is rejected while account is locked
- **Data Used**: 
  - Username: `junit_login_<timestamp>` (auto-generated)
  - Role: `Student`
  - Password: `pass123`
- **Expected Result**: Account locks after 5 consecutive failed login attempts

#### 1.2 User Registration & Credentials (AuthDaoTest)
**Test**: `testAddAndFindUser`
- **Purpose**: Verify user creation and lookup functionality
- **Steps**:
  1. Add new user via AuthDao with username, role, password
  2. Retrieve user by username
  3. Verify all user attributes are correctly stored
- **Data Used**:
  - Username: `junit_user_<timestamp>` (auto-generated)
  - Role: `Student`
  - Password: `pass123` (hashed via BCrypt)
- **Expected Result**: User is created with correct attributes and password hash

#### 1.3 User Status Management
**Related Tests**: `AuthDaoTest`, `AuthServiceTest`
- **Purpose**: Account status tracking (active, locked, inactive)
- **Test Data**:
  - Active users: `admin1`, `inst1`, `stu1`, `stu2`
  - Additional test users created dynamically

---

### 2. Course Management

#### 2.1 Course Creation (AdminServiceTest)
**Test**: `testAddCourseValidations`
- **Purpose**: Validate course creation rules and constraints
- **Steps**:
  1. Attempt to create course with negative credits → validation error
  2. Create course with valid data (code='X', title='Title', credits=3)
  3. Attempt duplicate course creation → duplicate error
- **Data Used**:
  - Course Code: `X`
  - Title: `Title`
  - Credits: `3` (valid), `-1` (invalid)
- **Expected Result**: 
  - Invalid credit values rejected
  - Valid courses created successfully
  - Duplicate course codes rejected

#### 2.2 Course Persistence (CourseDaoTest)
**Test**: `testDuplicateCourse`
- **Purpose**: Verify database-level course uniqueness constraint
- **Database Setup**: Uses real MySQL with cleanup
- **Steps**:
  1. Create course with code `CSE999`
  2. Attempt to create duplicate course with same code
  3. Verify duplicate is rejected
- **Data Used**:
  - Test Course Code: `CSE999`
  - Title: `JUnit Test Course`
  - Credits: `4`
- **Database Cleanup**: 
  - Before: DELETE FROM courses WHERE code='CSE999'
  - After: DELETE FROM courses WHERE code='CSE999'
- **Expected Result**: First insert succeeds, duplicate insert fails

#### 2.3 Course Retrieval (CourseDaoTest)
**Test**: `testGetCourseByCode`
- **Purpose**: Verify accurate course data retrieval
- **Steps**:
  1. Create course (CSE999, 'Algorithms', 3 credits)
  2. Retrieve course by code
  3. Verify all attributes match
- **Expected Result**: Course retrieved with correct code, title, and credits

#### 2.4 Course Existence Check (CourseDaoTest)
**Test**: `testExists`
- **Purpose**: Check if course exists in system
- **Steps**:
  1. Check non-existent course → false
  2. Create course
  3. Check same course again → true
- **Expected Result**: Accurate existence verification

---

### 3. Student Enrollment & Registration

#### 3.1 Section Registration (StudentServiceTest)
**Test**: `testRegisterAlreadyAndFull`
- **Purpose**: Validate enrollment constraints (duplicate prevention, capacity limits)
- **Setup**:
  - Course: `C1` (3 credits)
  - Section: 1 seat capacity
- **Steps**:
  1. Student 1 registers for section → "Registered"
  2. Student 1 attempts to re-register → "Already registered"
  3. Create second section with same capacity
  4. Student 1 registers for second section → "Registered"
  5. Student 2 attempts to register for full section → "Section full"
- **Data Used**:
  - Students: ID 1, ID 2
  - Course: `C1` (Title, 3 credits)
  - Sections: 2 sections, 1 seat each
  - Schedule: Thu 12-13, Room R404
  - Semester: Spring 2025
- **Expected Result**:
  - Duplicate registrations blocked
  - Over-capacity registrations blocked
  - Valid registrations succeed

#### 3.2 Drop Deadline Enforcement (StudentServiceTest)
**Test**: `testDropDeadlineValidation`
- **Purpose**: Enforce drop deadline policy
- **Steps**:
  1. Student registers for section
  2. Set drop deadline to yesterday (passed)
  3. Attempt drop → "Drop deadline passed"
  4. Set drop deadline to tomorrow (future)
  5. Attempt drop again → "Dropped"
- **Data Used**:
  - Student: ID 1
  - Setting: `drop_deadline` (date comparison)
  - Section: Same as 3.1 setup
- **Expected Result**:
  - Drops rejected after deadline
  - Drops allowed before deadline

#### 3.3 Enrollment Flow (EnrollmentDaoTest)
**Test**: `testEnrollAndDropFlow`
- **Purpose**: Verify enrollment state transitions
- **Steps**:
  1. Enroll student → true
  2. Check enrollment exists → true
  3. Count enrolled → 1
  4. Drop student → true
  5. Check enrollment exists → false
- **Data Used**:
  - Student: ID 1
  - Section: ID 1
- **Expected Result**: Enrollment and drop operations succeed with correct state tracking

---

### 4. Grade Management

#### 4.1 Grade Entry & Retrieval (GradeDaoTest)
**Test**: `testUpdateAndGetGrades`
- **Purpose**: Verify grade storage and component tracking
- **Steps**:
  1. Enter quiz grade (8.5) for component 'Quiz'
  2. Retrieve quiz grade → 8.5
  3. Enter final grade 'A'
  4. Verify final grade appears in enrollment grades
- **Data Used**:
  - Enrollment: ID 1
  - Component: `Quiz`
  - Score: `8.5`
  - Final Grade: `A`
  - Other Components: Midterm, Final Exam, Assignment, Participation
- **Expected Result**:
  - Component grades stored and retrieved correctly
  - Final letter grade tracked per enrollment

#### 4.2 Grade Weight Management
**Related Tests**: `GradeDaoTest`
- **Purpose**: Configure grading scheme by component
- **Data Used**:
  - Section ID: Unique identifier
  - Weights: e.g., {Quiz: 20, Midterm: 30, Final: 50}
- **Expected Result**: Weights saved and retrieved per section

---

### 5. System Settings & Maintenance

#### 5.1 Maintenance Mode Toggle (SettingDaoTest)
**Test**: `testMaintenanceToggle`
- **Purpose**: Enable/disable system maintenance mode
- **Steps**:
  1. Enable maintenance → isMaintenanceOn() = true
  2. Disable maintenance → isMaintenanceOn() = false
- **Data Used**:
  - Setting Key: `maintenance`
  - Values: `on` / `off`
- **Expected Result**: Maintenance mode toggles correctly

#### 5.2 Setting Update & Retrieval (SettingDaoTest)
**Test**: `testUpdateAndGetSetting`
- **Purpose**: Manage arbitrary system settings
- **Steps**:
  1. Update setting 'notice' to 'Hello'
  2. Retrieve setting by key
  3. Verify value matches
- **Data Used**:
  - Setting Key: `notice`
  - Value: `Hello`
- **Expected Result**: Settings stored and retrieved accurately

---

## Execution Environment

### Test Technologies
- **Framework**: JUnit 5 (Jupiter)
- **Database**: MySQL (for integration tests)
- **Password Hashing**: BCrypt (via jbcrypt library)
- **Test Utilities**: testutil/Fakes.java (in-memory DAOs)

### Running Tests

#### Run All Tests
```bash
mvn test
```

#### Run Specific Test Class
```bash
mvn test -Dtest=CourseDaoTest
mvn test -Dtest=AuthServiceTest
mvn test -Dtest=StudentServiceTest
```

#### Run Specific Test Method
```bash
mvn test -Dtest=CourseDaoTest#testDuplicateCourse
mvn test -Dtest=StudentServiceTest#testRegisterAlreadyAndFull
```

---

## Test Quality Assurance

### Database Integrity
- **No Persistent State**: All tests using fake DAOs leave zero database changes
- **Cleanup Strategy**: Integration tests (CourseDaoTest) use @BeforeEach and @AfterEach to delete test data before and after execution
- **Example Cleanup**:
  ```sql
  DELETE FROM courses WHERE code='CSE999'
  ```

### Test Isolation
- Each test is independent and can run in any order
- Fake DAO instances created fresh for each test (@BeforeEach)
- No shared mutable state between tests

### Coverage Areas
✓ Authentication & access control  
✓ Course CRUD operations  
✓ Student enrollment & registration  
✓ Drop deadline enforcement  
✓ Grade management  
✓ System settings & maintenance mode  
✓ Data persistence & cleanup  
✓ Input validation  
✓ Constraint enforcement  
✓ Concurrent enrollment scenarios  

---



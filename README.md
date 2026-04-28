# UniProBudget Called CashFlow
# YouTube Link
https://youtu.be/wBEEa59LWps
# GitHub Actions Automation And Mutlpe commits(ANOTHER REPOSITORY WAS USED TO COMMIT AND AUTOMATE EVEYTHING)
Location:
 https://github.com/seboge-Atlegang/Prog3C7313
### 1 Repository Checkout
https://github.com/seboge-Atlegang/Prog3C7313 
## Workflow File
Location:
https://github.com/seboge-Atlegang/Prog3C7313/tree/main/.github/workflows
# Informaton on the app 

UniProBudget caleed CashFlow is an Android budgeting and personal finance application developed in **Kotlin** using **Android Studio**. The application helps users manage income, expenses, savings goals and categories while storing data locally using **Room Database**.

This project was developed for **PROG7313 Part 2** and includes:

* Budget and expense management
* Savings goals tracking
* Categorised spending
* Reports/dashboard functionality
* Local database persistence with Room
* Automated testing
* GitHub Actions continuous integration (CI)

# Features
## 1. User Authentication
The application includes:
* User Registration ,User Login, Input validation for login and registration

Purpose:
* Secure user access
* Personalised budgeting records
---

## 2. Expense Tracking

Users can:
* Add expenses, Track spending
  
* Store:
  * Amount, Date, Start time, End time, Description, Category

Purpose:
* Monitor expenses
* Improve financial management
---

## 3. Categories and Goals

Users can:
* Create budget categories, Set spending goals, Track savings goals, Monitor category allocations

Examples:
* Food, Transport ,Entertainment, Savings
---

## 4. Reports Dashboard

The reporting module provides:
* Expense summaries, Spending insights, Budget monitoring, Goal progress tracking

---
# Technologies Used
## Frontend / Mobile

* Kotlin, Android SDK, Android Studio

## Database
* Room Database (SQLite), DAO pattern, Entity models

## Testing
* JUnit, Android Instrumented Tests, Database tests

## DevOps / Automation
* GitHub Actions
* Gradle
* Continuous Integration (CI)

---
# How the Application Works

## User Flow
### Step 1 — Login / Register
The user creates an account or logs in.

System validates:
* Required fields
* User credentials
* Input correctness

---

## Step 2 — Add Expenses
Users enter:
* Amount
* Category
* Description
* Date and time

The data is saved using Room Database.

---
## Step 3 — Manage Goals
Users:
* Define goals
* Assign categories
* Monitor progress
---

## Step 4 — View Reports
Dashboard generates summaries based on stored data.
---

# Database Implementation
This project uses **Room Database** for local persistence.
## Components
### Entities
Example:

* Expense
* Categories
* Goals

Entities represent database tables.
---

## DAO
Data Access Objects handle:

* Insert
* Update
* Query
* Delete

Example operations:

* Insert expense
* Retrieve expenses
* Generate reports

---

## AppDatabase

Acts as:

* Main database
* Connects entities and DAO

---

# How To Run The Project In Android Studio

## Requirements
Install:
* Android Studio (latest stable version)
* JDK 17
* Android Emulator
* Gradle (comes with Android Studio)
---

## Step 1 Clone Repository
Using Git:
```bash
git clone https://github.com/<your-username>/Prog3C7313.git
```
## Step 2 Open In Android Studio
1. Open Android Studio
2. Click:
```text
Open
```
3. Select:

```text
Prog3C7313/UniProBudget
```

4. Allow Gradle Sync to complete.

---

## Step 3 Configure Emulator

Create Android emulator:

Recommended:

* Pixel Device
* API 33 or higher

Android Studio:

```text
Tools → Device Manager → Create Device
```

---

## Step 4 Run App
Click:

```text
Run ▶
```

The app launches in emulator.

---


## Run Lint
```bash
./gradlew lint
```
---
# APK Location

Generated APK:

```text
app/build/outputs/apk/debug/
```
---
# Automated Testing
# GitHub Actions Automation And Mutlpe commits 
ANOTHER REPOSITORY WAS USED TO COMMIT AND AUTOMATE EVEYTHING 
Location:
 https://github.com/seboge-Atlegang/Prog3C7313
### 1 Repository Checkout
https://github.com/seboge-Atlegang/Prog3C7313 
## Workflow File
Location:
https://github.com/seboge-Atlegang/Prog3C7313/tree/main/.github/workflows
---
Automated Testing Implemented

GitHub Actions CI pipeline configured.
On each push GitHub automatically:
runs unit tests
runs database instrumentation tests
builds APK
DatabaseTest.kt validates Room database insert and retrieval logic.
This ensures the application runs successfully outside the local development machine.
Automation implemented using GitHub Actions includes:
- Automatic test execution
- Automated lint/code checks
- Automatic APK build verification
- Artifact generation
- Continuous Integration on every push
 
# Authors
Developed for:
PROG7313 Part 2
# Developers:
Ntokzo Khumalo – ST10447179
Mikhaeel Ismail – ST10433968
Siyabonga Nkomazana – ST10444685
Atlegang Keamogetswe Seboge – ST10443510

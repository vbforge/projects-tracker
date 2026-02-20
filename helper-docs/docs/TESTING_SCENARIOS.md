# 🧪 Project Tracker - Testing Scenarios

## Pre-Test Setup
- ✅ Database is clean or has test data
- ✅ App is running on `http://localhost:8080`
- ✅ You have 2 test user accounts ready (or will create them)

---

## 🔐 AUTHENTICATION & SECURITY

### Scenario 1: User Registration +++
**Steps:**
1. Navigate to `/register`
2. Fill in:
   - Username: `testuser1`
   - Email: `test1@example.com`
   - Password: `password123`
   - Confirm Password: `password123`
3. Click "Create Account"

**Expected Results:**
- ✅ Redirect to `/login` with success message
- ✅ Can see "Account created successfully!"

---

### Scenario 2: User Login +++
**Steps:**
1. Navigate to `/login`
2. Enter credentials:
   - Username: `testuser1`
   - Password: `password123`
3. Check "Remember me for 30 days"
4. Click "Sign In"

**Expected Results:**
- ✅ Redirect to `/projects` (dashboard)
- ✅ Navbar shows username "testuser1" in top-right
- ✅ "Logout" button visible
- ✅ Cookie `remember-me` set (check browser DevTools)

---

### Scenario 3: Protected Routes +++
**Steps:**
1. Log out
2. Try to access `/projects` directly
3. Try to access `/statistics`
4. Try to access `/tags`

**Expected Results:**
- ✅ All redirect to `/login`
- ✅ Cannot access protected pages without authentication

---

### Scenario 4: Data Isolation Between Users +++
**Steps:**
1. Login as `testuser1`
2. Create a project "Test Project 1"
3. Create a tag "Spring Boot"
4. Logout
5. Register and login as `testuser2`
6. Check dashboard

**Expected Results:**
- ✅ Dashboard shows 0 projects (not testuser1's project)
- ✅ No tags available (testuser1's tags not visible)
- ✅ Can create own tag "Spring Boot" (same name, no conflict)
- ✅ Complete data isolation

---

## 🏷️ TAG MANAGEMENT

### Scenario 5: Create Tag (Inline Form) +++
**Steps:**
1. Login as `testuser1`
2. Navigate to `/tags`
3. In "Create New Tag" section, fill:
   - Name: `Spring Boot`
   - Color: `#28a745` (green)
   - Description: `Java framework`
4. Click "Create"

**Expected Results:**
- ✅ Success message: "Tag created successfully!"
- ✅ Tag appears in table below
- ✅ Tag shows green color preview
- ✅ Project count shows `0`

---

### Scenario 6: Create Duplicate Tag (Should Fail) +++
**Steps:**
1. Try to create tag "Spring Boot" again
2. Click "Create"

**Expected Results:**
- ✅ Error message: "Tag 'Spring Boot' already exists!"
- ✅ Tag NOT created (duplicate prevention works)

---

### Scenario 7: Edit Tag (Modal) +++
**Steps:**
1. In tags table, click "Edit" (pencil icon) on "Spring Boot"
2. Modal opens, change:
   - Name: `Spring Boot 3`
   - Color: `#007bff` (blue)
   - Description: `Latest Spring version`
3. Click "Save"

**Expected Results:**
- ✅ Modal closes
- ✅ Success message: "Tag updated successfully!"
- ✅ Table shows updated name, color, description
- ✅ Color preview is now blue

---

### Scenario 8: Delete Unused Tag +++
**Steps:**
1. Create a new tag "TestTag"
2. Click "Delete" (trash icon) on "TestTag"
3. Confirm deletion in prompt

**Expected Results:**
- ✅ Success message: "Tag deleted successfully!"
- ✅ Tag removed from table
- ✅ Total tag count decreased by 1

---

### Scenario 9: Tag Popularity Ordering +++
**Steps:**
1. Create 3 tags: "React", "Docker", "PostgreSQL"
2. Create projects and assign tags:
   - Project 1: "React" + "Docker"
   - Project 2: "React"
   - Project 3: "Docker"
3. Go to `/tags`

**Expected Results:**
- ✅ Tags ordered by usage count:
   1. React (2 projects)
   2. Docker (2 projects)
   3. PostgreSQL (0 projects)

---

## 📁 PROJECT MANAGEMENT

### Scenario 10: Create Project (Basic) +++
**Steps:**
1. Navigate to `/projects/new`
2. Fill in:
   - Title: `Portfolio Website`
   - Description: `My personal portfolio`
   - Status: `IN_PROGRESS`
   - GitHub: checked
   - GitHub URL: `https://github.com/user/portfolio`
   - Local Path: `C:\Projects\portfolio`
3. Click "Create Project"

**Expected Results:**
- ✅ Redirect to `/projects` with success message
- ✅ Project appears in dashboard
- ✅ Status badge shows "IN PROGRESS" (yellow)
- ✅ GitHub icon visible

---

### Scenario 11: Create Project with Tags +++
**Steps:**
1. Navigate to `/projects/new`
2. Fill basic info:
   - Title: `E-commerce API`
3. Scroll to "Tags" section
4. Check: "Spring Boot", "PostgreSQL", "Docker"
5. Click "Create Project"

**Expected Results:**
- ✅ Project created
- ✅ Project card shows 3 tag badges
- ✅ Tag colors match tag definitions

---

### Scenario 12: Edit Project - Update Tags +++
**Steps:**
1. Click "Edit" on "E-commerce API"
2. Existing tags should show as CHECKED:
   - ✅ Spring Boot (checked)
   - ✅ PostgreSQL (checked)
   - ✅ Docker (checked)
3. Uncheck "Docker"
4. Check "React" (new tag)
5. Click "Update Project"

**Expected Results:**
- ✅ Success message
- ✅ Project now shows: Spring Boot, PostgreSQL, React
- ✅ Docker removed
- ✅ Previous tags preserved (not cleared)

---

### Scenario 13: Delete Project +++
**Steps:**
1. Click "Delete" on any project
2. Confirm deletion in popup

**Expected Results:**
- ✅ Success message: "Project deleted successfully!"
- ✅ Project removed from list
- ✅ Total project count decreased
- ✅ Tag still exists (not deleted with project)

---

### Scenario 14: View Project Details +++
**Steps:**
1. Click on project title to view details
2. Navigate to `/projects/{id}`

**Expected Results:**
- ✅ Shows all project information
- ✅ Creation date visible
- ✅ Last worked date visible
- ✅ All tags displayed
- ✅ "Edit" and "Delete" buttons available

---

## 🔍 FILTERING & SEARCH

### Scenario 15: Filter by Status +++
**Steps:**
1. Go to dashboard
2. Click "Status" dropdown
3. Select "IN PROGRESS"

**Expected Results:**
- ✅ URL shows `?status=IN_PROGRESS`
- ✅ Only IN_PROGRESS projects visible
- ✅ Filter badge shows "Filtered"
- ✅ Stats cards unchanged (show all projects)

---

### Scenario 16: Filter by GitHub Status +++
**Steps:**
1. Click "On GitHub" dropdown
2. Select "Yes"

**Expected Results:**
- ✅ URL shows `?onGithub=true`
- ✅ Only projects with GitHub icon visible
- ✅ Can combine with other filters

---

### Scenario 17: Search by Title +++
**Steps:**
1. Type "API" in search box
2. Press Enter

**Expected Results:**
- ✅ URL shows `?search=API`
- ✅ Only projects with "API" in title shown
- ✅ Case-insensitive search works

---

### Scenario 18: Filter by Tag (Single) +++
**Steps:**
1. Click on "Spring Boot" tag in filter section
2. Tag becomes highlighted

**Expected Results:**
- ✅ URL shows `?tags=Spring+Boot`
- ✅ Only projects with "Spring Boot" tag visible
- ✅ Selected tag highlighted in blue

---

### Scenario 19: Filter by Multiple Tags +++
**Steps:**
1. Click "Spring Boot" tag
2. Click "React" tag (add to filter)

**Expected Results:**
- ✅ URL shows `?tags=Spring+Boot&tags=React`
- ✅ Shows projects with EITHER tag (OR logic)
- ✅ Both tags highlighted

---

### Scenario 20: Clear All Filters +++
**Steps:**
1. Apply multiple filters (status + tags + search)
2. Click "Clear Filters" button

**Expected Results:**
- ✅ URL returns to `/projects`
- ✅ All projects visible again
- ✅ Filter badges disappear
- ✅ Tag highlights removed

---

### Scenario 21: Combine Filters +++
**Steps:**
1. Search: "Project"
2. Status: "IN_PROGRESS"
3. Tags: "Spring Boot"
4. GitHub: "Yes"

**Expected Results:**
- ✅ Shows projects matching ALL criteria (AND logic)
- ✅ URL contains all parameters
- ✅ Results update correctly

---

## 🔢 PAGINATION

### Scenario 22: Page Size Selection +++
**Steps:**
1. Create 30 projects (if not enough)
2. Default shows 10 per page
3. Click "25" in page size selector

**Expected Results:**
- ✅ URL shows `?size=25`
- ✅ Shows 25 projects per page
- ✅ "Showing 1 - 25 of 30 projects"
- ✅ Pagination bar updates

---

### Scenario 23: Navigate Pages +++
**Steps:**
1. Set page size to 10
2. Click "2" in pagination bar
3. Click "Next" button
4. Click "Previous" button

**Expected Results:**
- ✅ URL shows `?page=0` (first), `?page=1` (second), etc.
- ✅ Different projects shown on each page
- ✅ "Showing X - Y of Z" updates correctly
- ✅ "Previous" disabled on page 1
- ✅ "Next" disabled on last page

---

### Scenario 24: Pagination with Filters +++
**Steps:**
1. Apply filter: Status = "DONE"
2. Result: 8 projects
3. Set page size to 5
4. Navigate to page 2

**Expected Results:**
- ✅ URL: `?status=DONE&page=1&size=5`
- ✅ Shows projects 6-8
- ✅ Pagination works on filtered results
- ✅ Total shows "8 projects" (filtered count)

---

### Scenario 25: Changing Filter Resets Page +++
**Steps:**
1. Go to page 3 of results
2. Change status filter

**Expected Results:**
- ✅ Automatically returns to page 1 (`page=0`)
- ✅ Shows first results of new filter
- ✅ Prevents empty page errors

---

## 📊 STATISTICS DASHBOARD

### Scenario 26: View Statistics +++
**Steps:**
1. Navigate to `/statistics`
2. View the dashboard

**Expected Results:**
- ✅ 4 quick stat cards show:
   - Total Projects
   - Projects Completed
   - Projects in Progress
  - Completion Rate (%)
- ✅ 5 charts display:
   1. Status bar chart (red/yellow/green)
   2. GitHub vs Local pie chart
   3. Projects created (6-month timeline)
   4. Top 10 tags (horizontal bar)
   5. Activity heatmap (12 most stale projects)

---

### Scenario 27: Chart Interactions +++
**Steps:**
1. Hover over chart bars/segments
2. Click legend items
3. Toggle theme (light/dark)

**Expected Results:**
- ✅ Tooltips show on hover
- ✅ Clicking legend toggles data visibility
- ✅ Charts update colors with theme
- ✅ Responsive on mobile

---

### Scenario 28: Activity Heatmap Legend +++
**Steps:**
1. Look at Activity Heatmap chart
2. Check legend in top-right

**Expected Results:**
- ✅ Shows 3 colored circles:
   - 🟢 Active (0-7 days)
   - 🟡 Warm (8-30 days)
   - 🔴 Cold (30+ days)
- ✅ Projects colored by staleness

---

## 🎨 UI/UX FEATURES

### Scenario 29: Dark Mode Toggle +++
**Steps:**
1. Click sun/moon icon in navbar
2. Toggle between light and dark themes

**Expected Results:**
- ✅ Theme switches instantly
- ✅ All pages respect theme
- ✅ Charts update colors
- ✅ Preference saved in localStorage
- ✅ Persists after page reload

---

### Scenario 30: Responsive Design +++
**Steps:**
1. Resize browser window to mobile width (< 768px)
2. Check navbar, cards, tables, pagination

**Expected Results:**
- ✅ Navbar collapses to hamburger menu
- ✅ Cards stack vertically
- ✅ Table becomes horizontally scrollable
- ✅ Pagination shows "1 / 3" format
- ✅ No horizontal overflow

---

### Scenario 31: View Toggle (Cards/List) +++
**Steps:**
1. On dashboard, click "Cards" view
2. Click "List" view

**Expected Results:**
- ✅ Projects display as cards (default)
- ✅ Projects display as table rows
- ✅ View preference saved
- ✅ Active button highlighted

---

## 📤 EXPORT FEATURES

### Scenario 32: Export CSV +++
**Steps:**
1. Apply some filters (optional)
2. Click "Export" dropdown
3. Select "CSV"

**Expected Results:**
- ✅ File downloads: `projects_YYYYMMDD_HHMMSS_username_.csv`
- ✅ Contains filtered projects only
- ✅ CSV has headers
- ✅ Opens in Excel correctly

---

### Scenario 33: Export HTML +++
**Steps:**
1. Click "Export" → "HTML"

**Expected Results:**
- ✅ File downloads: `projects_report_[username]_YYYYMMDD_HHMMSS.html`
- ✅ Opens in browser
- ✅ Shows styled table
- ✅ Status badges colored
- ✅ Tags with color badges

---

## 🔄 SORTING

### Scenario 34: Sort by Last Worked +++
**Steps:**
1. Click "Sort" dropdown
2. Select "Last Worked" (default)

**Expected Results:**
- ✅ Projects ordered by `lastWorkedOn` DESC
- ✅ Most recent at top
- ✅ URL: `?sortBy=lastWorked`

---

### Scenario 35: Sort by Created Date +++
**Steps:**
1. Select "Created Date"

**Expected Results:**
- ✅ Newest projects first
- ✅ URL: `?sortBy=created`

---

### Scenario 36: Sort by Title (Alphabetical) +++
**Steps:**
1. Select "Title"

**Expected Results:**
- ✅ A-Z ordering
- ✅ Case-insensitive
- ✅ URL: `?sortBy=title`

---

## 🚨 ERROR HANDLING

### Scenario 37: Invalid Login +++
**Steps:**
1. Try to login with wrong password
2. Try non-existent username

**Expected Results:**
- ✅ Error message: "Invalid username or password"
- ✅ Stays on login page
- ✅ Form data NOT cleared (username kept)

---

### Scenario 38: Validation Errors +++
**Steps:**
1. Try to create project with empty title //todo: now it is possible, need fix
2. Try invalid email in registration //need to better validation of email because user@user is a valid, so need to fix better validation

**Expected Results:**
- ✅ Inline error messages below fields
- ✅ Fields highlighted in red
- ✅ Form NOT submitted
- ✅ Other field values preserved

---

### Scenario 39: Access Other User's Data +++
**Steps:**
1. Login as `testuser1`
2. Note a project ID, e.g., `/projects/5`
3. Logout, login as `testuser2`
4. Try to access `/projects/5/edit` directly

**Expected Results:**
- ✅ 404 Not Found OR redirect
- ✅ Cannot access other user's project
- ✅ Error logged in console

---

### Scenario 40: Delete Tag Used by Projects +++
**Steps:**
1. Create tag "ToDelete"
2. Assign it to a project
3. Try to delete the tag

**Expected Results:**
- ✅ Tag deleted successfully
- ✅ Project still exists
- ✅ Tag removed from project's tag list
- ✅ No orphaned data

---

## 🏁 FINAL INTEGRATION TEST

### Scenario 41: Complete User Journey +++
**Steps:**
1. Register as new user
2. Create 3 tags (Spring, React, Docker)
3. Create 5 projects with various tags
4. Filter by tag
5. Edit a project (add/remove tags)
6. View statistics
7. Export to CSV
8. Logout and login again
9. Verify all data persists

**Expected Results:**
- ✅ All operations work smoothly
- ✅ Data persists correctly
- ✅ No errors in console
- ✅ Remember-me works after re-login
- ✅ All features functional

---

## ✅ CHECKLIST SUMMARY

### Core Features
- [✅] Registration works
- [✅] Login works
- [✅] Logout works
- [✅] Remember-me works
- [✅] Data isolation between users
- [✅] Tag CRUD (create, read, update, delete)
- [✅] Project CRUD
- [✅] Tag assignment to projects
- [✅] Tag editing preserves selections

### Advanced Features
- [✅] Filtering (status, GitHub, search, tags)
- [✅] Multiple tag filters
- [✅] Pagination (size, navigation)
- [✅] Sorting (3 types)
- [✅] Statistics dashboard (5 charts)
- [✅] CSV export
- [✅] HTML export
- [✅] Dark mode
- [✅] Responsive design
- [✅] View toggle (cards/list)

### Security & Data
- [✅] Protected routes redirect to login
- [✅] Users cannot access others' data
- [✅] Tag names unique per user (not global)
- [✅] Session management works
- [✅] Logout clears session

### Edge Cases
- [✅] Empty states display correctly
- [✅] Validation prevents bad data
- [✅] Duplicate tags prevented per user
- [✅] Pagination on filtered results
- [✅] Filter changes reset page number
- [✅] Charts handle zero data
- [✅] Long tag names don't break UI
- [✅] Special characters in project titles

---

## 📝 NOTES

- Test on different browsers (Chrome, Firefox, Safari)
- Test on mobile device (real or DevTools)
- Check browser console for errors during testing
- Verify database constraints are correct
- Check all success/error messages display
- Ensure no data leaks between users

---

## 🎯 PASS CRITERIA

**Application passes if:**
- ✅ ALL 41 scenarios complete successfully
- ✅ No console errors during normal use
- ✅ Data isolation is perfect (zero leaks)
- ✅ All CRUD operations work
- ✅ UI is responsive and accessible
- ✅ Export functions work
- ✅ Statistics accurate

**Ready for production if:**
- ✅ All tests pass
- ✅ Performance acceptable (< 2s page loads)
- ✅ No memory leaks
- ✅ Security hardened (no SQL injection, XSS, etc.)

---


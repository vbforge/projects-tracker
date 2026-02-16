
## **Test Scenarios:**

### **Scenario 1: Status + GitHub + Tag**
1. Filter: IN_PROGRESS + On GitHub + Tag=REST API
2. **Screen shows:** 1 project
3. **Export CSV/HTML should show:** 1 project ✅

### **Scenario 2: Multiple Tags**
1. Filter: Tag=Concurrency + Tag=REST API
2. **Screen shows:** 4 projects
3. **Export CSV/HTML should show:** 4 projects ✅

### **Scenario 3: Sort Only**
1. No filters, Sort by Title (A-Z)
2. **Screen shows:** All 26 projects sorted alphabetically
3. **Export CSV/HTML should show:** All 26 projects in alphabetical order ✅

### **Scenario 4: Complex Combination**
1. Filter: Status=IN_PROGRESS + Search="Spring" + Tag=Docker
2. Sort by: Created Date
3. **Screen shows:** Filtered projects sorted by creation date
4. **Export should show:** Exact same filtered + sorted list ✅

---

## **Debugging Tips:**

If export still doesn't match, check the **URL in your browser's network tab** when clicking export:

Should look like:
```
/export/csv?status=IN_PROGRESS&onGithub=true&tags=REST%20API&sortBy=lastWorked
```

Or for multiple tags:
```
/export/csv?tags=Concurrency&tags=REST%20API&sortBy=title
```

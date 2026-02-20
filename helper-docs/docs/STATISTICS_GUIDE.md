# 📊 Statistics Dashboard Guide

---

## 📊 Features

### **5 Interactive Charts:**
1. **📊 Bar Chart** - Projects by Status (NOT_STARTED, IN_PROGRESS, DONE)
2. **🥧 Pie Chart** - GitHub vs Local Projects
3. **📈 Line Chart** - Projects Created Over Time (last 6 months)
4. **🏷️ Horizontal Bar** - Top 10 Most Used Tags
5. **🔥 Activity Heatmap** - Days Since Last Worked (color-coded)

### **4 Quick Stats Cards:**
- Total Projects
- Completed Count
- In Progress Count
- Completion Rate %

### **Interactive Features:**
- ✅ Hover tooltips on all charts
- ✅ Click legends to toggle data
- ✅ Auto-updates with dark mode toggle
- ✅ Responsive on all devices
- ✅ Professional color scheme

---

## ✅ Testing

### **1. Start Your Application**
```bash
mvn spring-boot:run
```

### **2. Access Statistics Dashboard**
Navigate to: **`http://localhost:8080/statistics`**

### **3. Click "Statistics" in Navbar**
The navbar already has the Statistics link ready!

### **4. Check Charts Display**
You should see:
- ✅ 4 stat cards at the top
- ✅ 5 beautiful charts with data
- ✅ Charts animate on load
- ✅ Hover shows tooltips
- ✅ Dark mode toggle works

---

## 🎨 What Each Chart Shows

### **1. Status Distribution Bar Chart**
- **Red Bar**: NOT_STARTED projects
- **Yellow Bar**: IN_PROGRESS projects
- **Green Bar**: DONE projects
- **Purpose**: See your project pipeline at a glance

### **2. GitHub vs Local Pie Chart**
- **Blue**: Projects on GitHub
- **Gray**: Local-only projects
- **Purpose**: See how much of your work is publicly hosted

### **3. Timeline Line Chart**
- **X-Axis**: Last 6 months (abbreviated month names)
- **Y-Axis**: Number of projects created
- **Purpose**: Track your project creation velocity

### **4. Top Tags Horizontal Bar**
- **Shows**: Up to 10 most-used tags
- **Sorted**: Most used at top
- **Colors**: Rainbow gradient
- **Purpose**: See which technologies you use most

### **5. Activity Heatmap**
- **Green (0-7 days)**: 🟢 Active projects
- **Yellow (8-30 days)**: 🟡 Warm projects
- **Red (30+ days)**: 🔴 Cold projects needing attention
- **Purpose**: Identify neglected projects

---

## 🔧 Customization

### **Change Chart Colors**

Edit `statistics.html`, find the chart configuration:

```javascript
// Example: Status Chart Colors
backgroundColor: [
    'rgba(220, 53, 69, 0.7)',   // NOT_STARTED - change to your color
    'rgba(255, 193, 7, 0.7)',   // IN_PROGRESS
    'rgba(25, 135, 84, 0.7)'    // DONE
]
```

### **Change Activity Heatmap Thresholds**

Edit `StatisticsServiceImpl.java` or adjust colors in the chart:

```javascript
// In statistics.html
if (value <= 7) return 'rgba(25, 135, 84, 0.7)';     // Green - Active
if (value <= 30) return 'rgba(255, 193, 7, 0.7)';    // Yellow - Warm
return 'rgba(220, 53, 69, 0.7)';                      // Red - Cold
```

### **Change Number of Top Tags**

Edit `StatisticsController.java`:

```java
Map<String, Long> topTags = statisticsService.getTopTags(10);  // Change to 5, 15, etc.
```

### **Change Number of Projects in Heatmap**

Edit `StatisticsController.java`:

```java
List<ProjectDTO> limitedActivity = activityData.stream()
        .limit(12)  // Change to show more/fewer projects
        .toList();
```

---

## 📱 Responsive Design

The dashboard is fully responsive:

**Desktop (1200px+)**
- 2 charts per row
- All cards visible
- Optimal chart sizes

**Tablet (768px - 1199px)**
- Charts stack responsibly
- Cards in 2 columns
- Still very readable

**Mobile (< 768px)**
- Single column layout
- Cards in 2x2 grid
- Charts resize perfectly
- Touch-friendly

---

## 🐛 Troubleshooting

### **Issue: No data in charts**
**Solution**: Make sure you have projects in your database. The charts need data to display!

### **Issue: Charts not loading**
**Solution**: 
1. Check browser console for errors
2. Verify Chart.js CDN is accessible
3. Clear browser cache (Ctrl+Shift+R)

### **Issue: Charts don't update with dark mode**
**Solution**: The theme toggle listener is in the statistics.html. Make sure the `themeToggle` element exists in the navbar.

### **Issue: Month names showing as YYYY-MM**
**Solution**: The controller converts these. Check that `TextStyle` and `Locale` imports are present.

### **Issue: Colors look wrong in dark mode**
**Solution**: The `getThemeColors()` function detects theme. Make sure `data-bs-theme` attribute is set correctly on `<html>`.

---

## 🎯 How It Works

### **Data Flow:**

1. **User visits `/statistics`**
2. **StatisticsController** calls **StatisticsService** methods
3. **StatisticsService** queries **ProjectRepository**
4. Service calculates:
   - Counts by status
   - GitHub vs local distribution
   - Monthly creation counts
   - Tag usage counts
   - Activity metrics
5. **Controller** formats data for charts
6. **Template** receives data via Thymeleaf
7. **Chart.js** renders interactive visualizations

---

## 💡 Pro Tips

### **Performance:**
- Service uses `@Transactional(readOnly = true)` for efficiency
- All calculations done in memory (fast)
- Charts render client-side (no server load)

### **Data Freshness:**
- Statistics are calculated on page load
- Refresh the page to see latest data
- No caching (always current)

### **Dark Mode:**
- Charts auto-update colors
- No page reload needed
- Professional look in both modes

---


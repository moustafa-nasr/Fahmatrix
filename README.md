# Fahmatrix

**Fahmatrix** is a lightweight, modern Java library for working with tabular data, inspired by Python's Pandas and rooted in the idea of making data understanding (*fahm*) easy on the JVM.

🚀 Intuitive API for tabular data  
📄 Easy CSV reading and previewing  
🔍 Row filtering and column selection  
📊 Aggregations, grouping, and sorting (coming soon)  
🧩 No external dependencies (for now)

---

## 🔧 Installation

### 📦 Using GitHub Releases

Visit [Releases](https://github.com/moustafa-nasr/fahmatrix/releases) and download the latest JAR file.

Include it manually in your project’s classpath or use Maven/Gradle if you're pulling from GitHub Packages (to be added in future versions).

If you're building locally:
```bash
git clone https://github.com/moustafa-nasr/fahmatrix.git
cd fahmatrix
./gradlew build
```

---

## 📚 Example Usage

```java
import com.fahmatrix.DataFrame;

public class Main {
    public static void main(String[] args) {
        DataFrame df = DataFrame.readCSV("data.csv");
        df.print();
    }
}
```

---

## ✨ Features

- Load CSV files into DataFrame
- Pretty-print data to console

### Coming Soon:
- View top rows with `head()`
- Filter rows and select columns
- Aggregations (sum, mean, etc.)
- GroupBy and pivot tables
- Data export to CSV or JSON
- Type inference and conversion

---

## 💡 Why Fahmatrix?

Java has lacked a clean, expressive DataFrame API — until now.

Fahmatrix brings data clarity (*fahm*) and structured thinking (*matrix*) together to give Java developers the tools they need to work with tabular data effectively, without leaving the JVM.

---

## 🙌 Support This Project

If you find Fahmatrix useful, consider [sponsoring me](https://github.com/sponsors/moustafa-nasr) to help support ongoing development, documentation, and future features.

---

## 📝 License

MIT License. Use it freely in your projects.
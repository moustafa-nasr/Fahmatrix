# Fahmatrix [![Version](https://img.shields.io/badge/version-0.1.3-yellow)](https://github.com/moustafa-nasr/fahmatrix/releases)
[![Docs](https://img.shields.io/badge/Javadoc-online-blue)](https://moustafa-nasr.github.io/Fahmatrix/)  [![Java](https://img.shields.io/badge/Java-17+-brightgreen?logo=java)](https://openjdk.org/) [![Platform](https://img.shields.io/badge/Platform-JVM-lightgrey)]() [![License](https://img.shields.io/github/license/moustafa-nasr/fahmatrix)](https://github.com/moustafa-nasr/fahmatrix/blob/main/LICENSE)
[![Star](https://img.shields.io/github/stars/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/stargazers) [![Fork](https://img.shields.io/github/forks/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/forks) [![Watch](https://img.shields.io/github/watchers/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/watchers)

[![Tweet](https://img.shields.io/badge/Tweet-Fahmatrix-blue?logo=twitter)](https://twitter.com/intent/tweet?text=Just%20discovered%20Fahmatrix%20%E2%80%94%20a%20lightweight%2C%20Pandas-like%20Java%20library%20for%20tabular%20data%20%F0%9F%93%8A%F0%9F%94%A5%0Ahttps%3A%2F%2Fgithub.com%2Fmoustafa-nasr%2Ffahmatrix)




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

## 📜 Docs

You can find compiled Java Docs [over here](https://moustafa-nasr.github.io/Fahmatrix/)

---

## ✨ Features

- Load CSV files into DataFrame
- Pretty-print data to console
- View top rows with `head()` or bottom ones with `tail()`
- Aggregations (count, min, max, sum, mean ,median, standard deviation, 25%, 50%, 70%, custom percentage)

### Coming Soon:

- Filter rows and select columns
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


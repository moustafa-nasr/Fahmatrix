# Fahmatrix [![Version](https://img.shields.io/badge/version-0.1.6-yellow)](https://github.com/moustafa-nasr/fahmatrix/releases)
[![Docs](https://img.shields.io/badge/Javadoc-online-blue)](https://moustafa-nasr.github.io/Fahmatrix/)  [![Java](https://img.shields.io/badge/Java-17+-brightgreen?logo=java)](https://openjdk.org/) [![Platform](https://img.shields.io/badge/Platform-JVM-lightgrey)]() [![License](https://img.shields.io/github/license/moustafa-nasr/fahmatrix)](https://github.com/moustafa-nasr/fahmatrix/blob/main/LICENSE)


[![DeepSource](https://app.deepsource.com/gh/moustafa-nasr/Fahmatrix.svg/?label=code+coverage&show_trend=true&token=6ViFroO6vI_7nN0kHPXFIbh4)](https://app.deepsource.com/gh/moustafa-nasr/Fahmatrix/)[![](https://jitpack.io/v/moustafa-nasr/Fahmatrix.svg)](https://jitpack.io/#moustafa-nasr/Fahmatrix/)[![Maven Central](https://img.shields.io/maven-central/v/com.fahmatrix/fahmatrix?label=Maven%20Central)](https://central.sonatype.com/artifact/com.fahmatrix/fahmatrix)


[![Star](https://img.shields.io/github/stars/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/stargazers) [![Fork](https://img.shields.io/github/forks/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/forks) [![Watch](https://img.shields.io/github/watchers/moustafa-nasr/fahmatrix?style=social)](https://github.com/moustafa-nasr/fahmatrix/watchers)

[![Tweet](https://img.shields.io/badge/Tweet-Fahmatrix-blue?logo=twitter)](https://twitter.com/intent/tweet?text=Just%20discovered%20Fahmatrix%20%E2%80%94%20a%20lightweight%2C%20Pandas-like%20Java%20library%20for%20tabular%20data%20%F0%9F%93%8A%F0%9F%94%A5%0Ahttps%3A%2F%2Fgithub.com%2Fmoustafa-nasr%2Ffahmatrix)

**Fahmatrix**  is a lightweight, modern Java library for working with tabular data — inspired by Python’s Pandas, but designed specifically for the JVM. It’s early in development, but already offers a clean API for loading, exploring, and manipulating data with zero external dependencies.

Ideal for small projects, backend systems, or embedded environments like Android, Fahmatrix is built to bring structured data handling to every corner of the Java ecosystem.


🚀 Intuitive API for tabular data  
📄 Easy CSV, Xlsx, Ods, Json reading and previewing  
📄 Easy CSV, Xlsx, Ods, Json writing  
🔍 Row filtering and column selection  
🔍 Column filtering by string operations (contains, equals, etc..)  
📊 Aggregations (mean , average , etc.. )  
📊 Grouping, and sorting (coming soon)  
🧩 No external dependencies (for now)

---

## 🔧 Installation

### 📦 Using GitHub Releases

Visit [Releases](https://github.com/moustafa-nasr/fahmatrix/releases) and download the latest JAR file.
<br>
Include it manually in your project’s classpath

### 🏠 If you're building locally:
```bash
git clone https://github.com/moustafa-nasr/fahmatrix.git
cd fahmatrix
./gradlew build
```

### or Test online

[![Open In Colab](https://colab.research.google.com/assets/colab-badge.svg)](https://colab.research.google.com/github/moustafa-nasr/Fahmatrix/blob/main/FahmatrixExample.ipynb)


### 🛠️ Maven

Add to pom.xml

```xml
<dependency>
  <groupId>com.fahmatrix</groupId>
  <artifactId>fahmatrix</artifactId>
  <version>0.1.6</version>
</dependency>
```

### 🛠️ Gradle (kotlin)

Add to build.gradle.kts

```kotlin
dependencies {
  implementation("com.fahmatrix:fahmatrix:0.1.6")
}

```

### 🛠️ Gradle (java)

Add to build.gradle

```java
dependencies {
  implementation 'com.fahmatrix:fahmatrix:0.1.6'
}

```

### 🛠️ Maven Using jitpack.io 

Add to pom.xml

```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

  	<dependency>
	    <groupId>com.github.moustafa-nasr</groupId>
	    <artifactId>Fahmatrix</artifactId>
	    <version>v0.1.6</version>
	</dependency>

```

### 🛠️ Gradle Using jitpack.io 

For Java add to build.gradle

```java

    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

    dependencies {
	        implementation 'com.github.moustafa-nasr:Fahmatrix:v0.1.6'
	}

```

For Kotlin add to build.gradle.kts

```kotlin

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}

    dependencies {
	        implementation("com.github.moustafa-nasr:Fahmatrix:v0.1.6")
	}

```


---

## 📚 Simple Example Usage

```java
import com.fahmatrix.DataFrame;

public class Main {
    public static void main(String[] args) {
        // read csv file
        DataFrame df = DataFrame.readCSV("data.csv");
        // pretty print data in system console
        df.print();
        // Pretty Print Data Summary in System Console
        // count, min, max, sum, mean ,median, standard deviation, 25%, 50%, 70%
        df.describe();
        // select certain rows and colums
        DataFrame result = df.select().rows(new int[]{1,2,3,5,6,8,110,10000,99}).columns(new String[]{"name","company","city"}).get();
        // save the final data as JSON format 
        result.writeJson("output.json");
        // save the final data as Microsoft Excel
        result.writeXlsx("output.xlsx");
        // save the final data as OpenDocument Spreadsheet
        result.writeOds("output.ods");
        // pretty print the last 3 rows
        result.tail(3).print();
    }
}
```
---

## 🆚 Simple Comparison

| Library      | Read Files | Aggregations | Notes             |
| ------------ | ---------- | ------------ | ----------------- |
| Fahmatrix    | ✓  	    | ✓           | Pure Java         |
| Tablesaw     | ✓  	    | ✓           | More dependencies |
| Apache Arrow | ✓  	    | ✕           | Requires setup    |
| Pandas       | ✓  	    | ✓           | Python-only       |

---
## 📜 Docs

You can find compiled Java Docs [over here](https://moustafa-nasr.github.io/Fahmatrix/)

---

## ✨ Features

- Load CSV, JSON, Microsoft Excel,Open office ODS files into DataFrame
- Save CSV, JSON, Microsoft Excel,Open office ODS files
- Pretty-print data to console
- View top rows with `head()` or bottom ones with `tail()`
- Tranculate Data
- Aggregations (count, min, max, sum, mean ,median, standard deviation, 25%, 50%, 70%, custom percentage)
- Filter data by String operations (contains,  equal, equal ignore case, start with , end with, regex , not empty, custom String Predicate)

### Coming Soon:

- Filter data by arithmetic operations (gt, lt, eq, neq)
- Filter data by Logic operations (and, or, not)
- GroupBy and pivot tables
- Nested JSON Data
- Data import/export for HTML, Xml, Parquet and more ..
- Type inference and conversion
- DSLInterpreter for SQL language lovers

---

## 💡 Why Fahmatrix?

Java has long lacked a clean, expressive DataFrame API — especially one that feels at home on the JVM.

Fahmatrix is an early-stage project that brings together data clarity (fahm) and structured thinking (matrix) to offer a lightweight, embeddable solution for tabular data processing in Java. Inspired by the elegance of tools like Pandas, Fahmatrix is built from the ground up to serve Java developers — whether you're building small utilities, backend services, or Android apps.

It’s still early days, but the goal is clear: a fast, intuitive, dependency-free DataFrame library that works where Java works.

---

## 🙌 Support This Project

If you find Fahmatrix useful, consider [sponsoring me](https://github.com/sponsors/moustafa-nasr) to help support ongoing development, documentation, and future features.

---

## 📝 License

MIT License. Use it freely in your projects.
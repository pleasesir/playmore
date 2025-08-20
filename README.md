# PlayMore Game Server 🎮

[![JDK Version](https://img.shields.io/badge/JDK-21-blue)](https://openjdk.org/projects/jdk/21/)
[![Vert.x Version](https://img.shields.io/badge/Vert.x-4.5-red)](https://vertx.io/)

Next-generation high-performance Java game server solution based on coroutine architecture

## 📖 Project Background

### Technical Evolution Path

- **Traditional Architecture** (Before JDK21)  
  Using asynchronous programming + event listener pattern, facing the following pain points:
    - Callback hell leading to poor code maintainability
    - High overhead of thread context switching
    - Low resource utilization

- **New Era Architecture** (JDK21+)  
  Combining Loom project's virtual threads with Vert.x's Actor model:
    - 🚀 Virtual threads implementing lightweight coroutines (one coroutine per request)
    - ⚡ Vert.x providing efficient Actor model implementation
    - 💡 Spring Boot 3.4 + Dubbo 3.3 building microservice ecosystem

## 🛠️ Technology Stack

### Core Components

| Component    | Version | Responsibility            |
|--------------|---------|---------------------------|
| OpenJDK      | 21      | Virtual threads/Structured concurrency |
| Vert.x Core  | 4.5.13  | Event bus/Actor model     |
| Spring Boot  | 3.4.2   | Dependency injection/Configuration center |
| Apache Dubbo | 3.3.3   | RPC framework/Service governance |

## 📚 Architecture Diagram

![img_2.png](img_2.png)(https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/config/spring/spring-boot/)
![img_1.png](img_1.png)(https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/registry/nacos/#12-nacos-%E7%89%88%E6%9C%AC)

### Architecture Features

## 📅 Version Evolution

### 2025.04.08 (build-2100)

- [✅] Basic code migration to new framework completed
- [✅] Improved some common dependency logic

### 2025.03.10 (build-2100)

- [✅] Improved some common dependency logic
- [✅] Completed manager service migration and refactoring

### 2025.03.08 (build-2100)

- [✅] Improved some common dependency logic
- [✅] Completed fight service migration and refactoring

### 2025.03.07 (build-2100)

- [✅] Improved some common dependency logic
- [✅] Completed account server refactoring

### 2025.02.27 (build-2100)

- [✅] Completed chat module stress testing

### 2025.02.21 (build-2100)

- [✅] Completed chat module refactoring
- [🔄] Battle system coroutine transformation (in progress)
- [📊] Added performance monitoring埋点

### 2025.02.14 (build-1840)

- [⬆️] Upgraded to Spring Boot 3.4.2
- [🔗] Integrated Dubbo 3.3.3, Vert.x 4.5.13
- [🎯] Established architecture prototype

---

> **Architecture Philosophy**: Achieve `1:1` request-coroutine mapping through virtual threads, combined with Vert.x's Actor model, to reach higher concurrent performance while maintaining synchronous programming style. 🔥

## 🚀 Quick Start

## 📚 Stress Test Results

![img.png](img.png)
Protocol test content:

- 8000 players log in simultaneously
- 8000 players send messages simultaneously:
  cmd: 1113 [DoSomeRq.ext]
  {
  str: 'resourceChange 1 1 10000'
  }

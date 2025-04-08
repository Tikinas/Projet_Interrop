cmake_minimum_required(VERSION 3.10)
project(DistributionImage)

set(CMAKE_CXX_STANDARD 17)

# Inclure le dossier courant (pour CImg.h)
include_directories(${CMAKE_SOURCE_DIR})

add_executable(DistributionImage src/main.cpp)

# Pour CImg (utilise X11)
find_package(X11 REQUIRED)
target_link_libraries(DistributionImage ${X11_LIBRARIES})
include_directories(${X11_INCLUDE_DIR})

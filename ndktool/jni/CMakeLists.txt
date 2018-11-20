cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library. so库名字
             ad_ndktool

             # Sets the library as a shared library.
             SHARED

             # 具体的C++ 文件
            src/AdNdktool.cpp

            )


find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )


target_link_libraries( # Specifies the target library.
        ad_ndktool

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

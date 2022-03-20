# Tess4J Example

This is a simple, _working_ example of optical character recognition (OCR). This is about showing
a picture of text to the software and the software determining what the text is, returned in the
form of a java.lang.String object.

The most popular, open source solution is [Tesseract](https://github.com/tesseract-ocr/tesseract "Tesseract") &copy;,
but this is a program written in C code.
For us Java Developers, we have a few JNI wrappers already made available to the community for
free use. This codebase demonstrates usage of what may be the most popular one; [Tess4J](http://tess4j.sourceforge.net/) &copy;.

This codebase demonstrating Tess4J came about by the frustration of not finding a simple, _working_ example.
The Tess4J site has you pull down old code meant to be built with Ant instead of Maven or Gradle.
Even after doing so, the required Tesseract data file(s) were absent. Then, [this blog post](http://www.microshell.com/programming/java/performing-optical-character-recognition-in-java/)
of Tess4J available from Maven Central resolved the build challenges with the Ant distribution. But that blog's 
advice how to resolve the Tesseract data file requirement is a hack and even the advice by the Tess4J 
author himself in that blog's feedback comment is not desirable. This codebase demonstrates encapsulating the Tesseract data file so that the code _just works_ without bothering with external conditions like setting a special system environment variable. Of course though; you still need the machine-specific C program of Tesseract
installed separately, beforehand.

I hope this is reference code you've been looking for. I sure wish someone came along before me
and volunteered this for us all! This codebase demonstration of Tess4J just begins exploring the Tess4J API. Therefore, I invite you to help grow this codebase with more test-proven examples and explanatory 
comments, so show me your pull requests.


## Prerequisites
This software still depends on Tesseract being installed. For example, on a Mac:

	brew install tesseract
## Troubleshooting
This software was pulled down from GitHub on a Mac that already had installed:
*   Tesseract 3.04.01_2
*   Leptonica 1.73

But the build was failing with the message:

> Unable to load library 'tesseract': Native library (darwin/libtesseract.dylib) not found in resource path

After upgrading Tesseract to a more recent release (which also upgraded a more recent Leptonica):

	brew upgrade tesseract

The following revisions are installed on the machine:

	brew list tesseract leptonica
*   Tesseract 3.05.01
*   Leptonica 1.74.04_1

Now, this Tess4J Example codebase successfully builds with its tests passing.

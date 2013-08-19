Running the DigitalSignatures Sample
------------------------------------

By default the DigitalSignatures sample is disabled because it is necessary
to install third party libraries. In order to run it, you will need to download
and install the Spongy Castle libraries.

You can download them here:

http://rtyley.github.com/spongycastle/

Place all the .jar files in the 'libs' folder, along with PDFNet.jar. Also,
make the following changes in the code to enable the sample:

- MySignatureHandler.java: Uncomment the imports and the createSignature()
method.
- DigitalSignaturesSample.java: comment the line DisableRun();

With these changes the sample should run without problems.

If you have questions on how to set up the project in Eclipse, please look
at the readme.html.
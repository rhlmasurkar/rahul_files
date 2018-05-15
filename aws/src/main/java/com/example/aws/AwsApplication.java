package com.example.aws;

import ch.qos.logback.core.util.FileUtil;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.UUID;

public class AwsApplication {

	public static void main(String[] args) throws IOException{

		String clientRegion = "us-east-2";
		String bucketName = "rahulemeter";
		String filePath = "C:\\mydocs\\int_planning\\applcnts\\new_java_tuts\\spring_aws\\test.txt";
		//String filePath1 = "C:\\mydocs\\int_planning\\applcnts\\new_java_tuts";



		AWSCredentials credentials = null;

		credentials = new ProfileCredentialsProvider().getCredentials();

			AmazonS3 s3 = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials))
					.withRegion(clientRegion)
					.build();

			try

			{
				//list S3 buckets
					System.out.println("listing S3 buckets");
				for (Bucket bucket : s3.listBuckets()) {
					System.out.println("the buckets are " + bucket);
				}

				//Upload objects to S3 buckets
				System.out.println("Uploading a new object to S3 from a file to bucket " + bucketName);
				s3.putObject(new PutObjectRequest(bucketName, "\"Document/test.txt\"", new File(filePath)));

				//listing Objects
				ObjectListing objectListing = s3.listObjects(bucketName);
				for(S3ObjectSummary os : objectListing.getObjectSummaries()) {
					System.out.println("the objects in bucket " + bucketName + " are " + os.getKey());
				}

				//Download Objects from S3 buckets
				System.out.println("Downloading an object");
				S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, "\"Document/test.txt\""));
				System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
				System.out.println("Content: ");
				displayTextInputStream(s3object.getObjectContent());

				//deleting an object
				s3.deleteObject(bucketName, "\"Document/test.txt\"");

			}
			catch(AmazonServiceException ace)

			{
				System.out.println("the exception is " + ace);
			}

	}
	private static void displayTextInputStream(InputStream input) throws IOException {
		// Read the text input stream one line at a time and display each line.
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line = null;
		while ((line = reader.readLine()) != null) {
			System.out.println("the contents of the file are " + line);
		}
		System.out.println();
	}

}

# Orchestrate Stacks with Cloudformation

## Description

Here I explain how to use nested stacks to orchestrate stacks creation in Cloudformation.

You can find a full explanation of this example in: [https://godof.cloud/orchestrate-stacks-cloudformation/](https://godof.cloud/orchestrate-stacks-cloudformation/)

## Requirements

An S3 Bucket to copy the templates and artifacts folders

## Content

- artifacts/lambda-tags.jar: JAR package with the lambda function already built.

- lambda-tags: Lambda tags function source code.

- templates/macros.yml: The cloudformation template that creates the tags macro (Must be created first to continue with the example).
  
  

- templates/master.yml: Master template that orchestrates the creation of the security groups and ec2 stacks

- templates/security-group-stack.yml: Template that creates a Security Group.

- templates/ec2-stack.yml: Template that creates an EC2 instance.

- artifacts/lambda-tags.jar: JAR package with the lambda function already built.

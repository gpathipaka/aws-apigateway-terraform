variable "aws_access_key" {
  # set aws access key
  default = ""
}

variable "aws_secret_key" {
  # set aws secret key
  default = ""
}

variable "region" {
  # set aws region
  default = "us-east-1"
}

variable "lambda_payload_filename" {
  default = "../cms-app/target/cms-app-1.0-SNAPSHOT.jar"
}

variable "lambda_function_handler" {
  default = "com.cms.app.LambdaMethodHandler"
}

variable "lambda_runtime" {
  default = "java11"
}

variable "api_path" {
  default = "{proxy+}"
}

variable "api_env_stage_name" {
  default = "terraform-lambda-java-stage"
}

variable "table_name" {
  default = "cms-data-table"
}

variable "table_billing_mode" {
  default = "PAY_PER_REQUEST"
}

variable "environment" {
  description = "Name of environment"
  default = "test"
}
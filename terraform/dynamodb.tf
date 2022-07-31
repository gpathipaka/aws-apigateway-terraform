resource "aws_dynamodb_table" "cms-data-table" {
  name        = "${var.table_name}"
  billing_mode = "${var.table_billing_mode}"
  hash_key       = "recordId"
  attribute {
    name = "recordId"
    type = "S"
  }
   tags = {
    environment       = "${var.environment}"
  }
}
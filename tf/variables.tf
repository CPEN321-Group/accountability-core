variable "region" {
    default = "us-east-1"
    description = "AWS region"
}

resource "random_string" "suffix" {
  length  = 8
  special = false
}

locals {
  cluster_name = "accountability-eks-${random_string.suffix.result}"
}
resource "kubernetes_deployment" "backend" {
  metadata {
    name = "accountability-backend"
    labels = {
      App = "AccountabilityBackend"
    }
  }

  spec {
    replicas = 1
    selector {
      match_labels = {
        App = "AccountabilityBackend"
      }
    }
    template {
      metadata {
        labels = {
          App = "AccountabilityBackend"
        }
      }
      spec {
        container {
          image = "381490268551.dkr.ecr.us-east-1.amazonaws.com/accountability:1.1.0"
          name  = "accountability-backend"

          port {
            container_port = 8000
          }

          resources {
            limits = {
              cpu    = "0.5"
              memory = "512Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "50Mi"
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "backend-lb" {
  metadata {
    name = "accountability-backend-lb"
  }
  spec {
    selector = {
      App = kubernetes_deployment.backend.spec.0.template.0.metadata[0].labels.App
    }
    port {
      port        = 80
      target_port = 8000
    }

    type = "LoadBalancer"
  }
}
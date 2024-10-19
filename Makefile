# Variables
IMAGE_NAME = heartbeatrr
IMAGE_TAG = latest
DOCKERFILE_PATH = Dockerfile
TAR_FILE_NAME = $(IMAGE_NAME)_$(IMAGE_TAG).tar

# Default target: Build the Docker image
build:
	@echo "Building Docker image: $(IMAGE_NAME):$(IMAGE_TAG)..."
	docker build -t $(IMAGE_NAME):$(IMAGE_TAG) -f $(DOCKERFILE_PATH) .

# Save the image as a .tar file
save:
	@echo "Saving Docker image as $(TAR_FILE_NAME)..."
	docker save -o $(TAR_FILE_NAME) $(IMAGE_NAME):$(IMAGE_TAG)

# Build and save the image
build_and_save: build save
	@echo "Docker image built and saved as $(TAR_FILE_NAME)."

# Clean up the generated .tar file
clean:
	@echo "Cleaning up..."
	rm -f $(TAR_FILE_NAME)

# Docker push (optional, for pushing to a registry)
push:
	@echo "Pushing Docker image $(IMAGE_NAME):$(IMAGE_TAG)..."
	docker push $(IMAGE_NAME):$(IMAGE_TAG)

# Default target (can be specified as the first one)
default: build_and_save
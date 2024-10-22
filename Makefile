# Variables
DOCKER_USER = moonscape1840
IMAGE_NAME = heartbeatrr
DOCKERFILE_PATH = Dockerfile
TAR_FILE_NAME = $(IMAGE_NAME)_$(IMAGE_TAG).tar

# Version is either passed in as an argument or read from a VERSION file
VERSION_FILE = VERSION
IMAGE_VERSION ?= $(shell cat $(VERSION_FILE))
IMAGE_TAG = $(IMAGE_VERSION)

# Full image name with Docker Hub username
FULL_IMAGE_NAME = $(DOCKER_USER)/$(IMAGE_NAME)

# Default target: Build the Docker image with version and latest tag
build:
	@echo "Building Docker image: $(FULL_IMAGE_NAME):$(IMAGE_TAG) and tagging as latest..."
	docker build -t $(FULL_IMAGE_NAME):$(IMAGE_TAG) -t $(FULL_IMAGE_NAME):latest -f $(DOCKERFILE_PATH) .

# Save the image as a .tar file with version tag
save:
	@echo "Saving Docker image as $(TAR_FILE_NAME)..."
	docker save -o $(TAR_FILE_NAME) $(FULL_IMAGE_NAME):$(IMAGE_TAG)

# Build and save the image
build_and_save: build save
	@echo "Docker image built and saved as $(TAR_FILE_NAME)."

# Clean up the generated .tar files
clean:
	@echo "Cleaning up..."
	rm -f $(TAR_FILE_NAME)

# Docker push to publish both the versioned image and the latest tag
push:
	@echo "Pushing Docker image $(FULL_IMAGE_NAME):$(IMAGE_TAG) and $(FULL_IMAGE_NAME):latest to registry..."
	docker push $(FULL_IMAGE_NAME):$(IMAGE_TAG)
	docker push $(FULL_IMAGE_NAME):latest

# Increment the version number (minor version bump)
bump_version:
	@echo "Bumping version number..."
	$(eval NEW_VERSION=$(shell echo $(IMAGE_VERSION) | awk -F. '{$$NF++; print $$0}' OFS=.))
	@echo $(NEW_VERSION) > $(VERSION_FILE)
	@echo "Version bumped to $(NEW_VERSION)."

# Build, save, and push the image
release: build_and_save push bump_version
	@echo "Release completed for $(FULL_IMAGE_NAME):$(IMAGE_TAG)."

# Default target: Build, save, push, and bump the version
default: build_and_save push bump_version
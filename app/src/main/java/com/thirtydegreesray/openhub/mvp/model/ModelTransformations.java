package com.thirtydegreesray.openhub.mvp.model;

import com.thirtydegreesray.openhub.FetchRepositoriesQuery;
import com.thirtydegreesray.openhub.fragment.OrganizationFragment;
import com.thirtydegreesray.openhub.fragment.RepositoryFragment;
import com.thirtydegreesray.openhub.fragment.UserFragment;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

public class ModelTransformations {

    public static Release transformRelease(final FetchRepositoriesQuery.Edge apolloRelease) {

        Release release = new Release();
        final FetchRepositoriesQuery.Node node = apolloRelease.node();
        release.setBody(node.description());
        release.setId(node.id());
        release.setDraft(node.isDraft());
        release.setPreRelease(node.isPrerelease());
        release.setName(node.name());
        release.setCreatedAt(node.createdAt());
        release.setPublishedAt(node.publishedAt());
        release.setTagName(node.tag().name());
        release.setAuthor(transformUser(node.author().fragments().userFragment()));
        release = setSourceUrls(release, node.tag());

        final List<ReleaseAsset> releaseAssets = new ArrayList<>();
        for (FetchRepositoriesQuery.Edge1 edge : node.releaseAssets().edges()) {
            releaseAssets.add(transformReleaseAsset(edge.node().fragments().releaseAsset()));
        }

        release.setAssets(releaseAssets);

        return release;
    }

    public static Release setSourceUrls(final Release release, final FetchRepositoriesQuery.Tag tag) {

        if (tag.target().fragments().tagCommit() == null) {
            release.setTarballUrl(tag.target().fragments().tag().target().fragments().tagCommit().tarballUrl().toString());
            release.setZipballUrl(tag.target().fragments().tag().target().fragments().tagCommit().zipballUrl().toString());
        } else {
            release.setTarballUrl(tag.target().fragments().tagCommit().tarballUrl().toString());
            release.setZipballUrl(tag.target().fragments().tagCommit().zipballUrl().toString());
        }

        return release;
    }

    public static ReleaseAsset transformReleaseAsset(final com.thirtydegreesray.openhub.fragment.ReleaseAsset dataReleaseAsset) {

        ReleaseAsset transformedAsset = new ReleaseAsset();

        transformedAsset.setContentType(dataReleaseAsset.contentType());
        transformedAsset.setCreatedAt(dataReleaseAsset.createdAt());
        transformedAsset.setDownloadCout(dataReleaseAsset.downloadCount());
        transformedAsset.setDownloadUrl(dataReleaseAsset.downloadUrl().toString());
        transformedAsset.setId(dataReleaseAsset.id());
        //transformedAsset.setLabel(dataReleaseAsset.);
        transformedAsset.setName(dataReleaseAsset.name());
        transformedAsset.setSize(dataReleaseAsset.size());
        //transformedAsset.setState(dataReleaseAsset);
        transformedAsset.setUpdatedAt(dataReleaseAsset.updatedAt());
        transformedAsset.setUploader(transformUser(dataReleaseAsset.uploadedBy().fragments().userFragment()));

        return transformedAsset;
    }

    public static User transformUser(final UserFragment userFragment) {

        User user = new User();
        user.setAvatarUrl(userFragment.avatarUrl().toString());
        user.setBio(userFragment.bio());
        //user.setBlog(userFragment.);
        user.setCompany(userFragment.company());
        user.setCreatedAt(userFragment.createdAt());
        user.setEmail(userFragment.userEmail());
        user.setFollowers(userFragment.followers().totalCount());
        user.setFollowing(userFragment.following().totalCount());
        //user.setLocation(userFragment.location());
        user.setId(userFragment.id());
        user.setLogin(userFragment.login());
        user.setName(userFragment.name());
        user.setPublicGists(userFragment.publicGists().totalCount());
        user.setPublicRepos(userFragment.publicRepos().totalCount());

        return user;
    }

    public static Repository transformRepository(final RepositoryFragment repositoryFragment, final RepositoryFragment parentFragment) {

        Repository transformedRepo = new Repository();

        transformedRepo.setId(valueOf(repositoryFragment.databaseId()));
        //transformedRepo.setCloneUrl(repositoryFragment);
        transformedRepo.setCreatedAt(repositoryFragment.createdAt());
        transformedRepo.setDefaultBranch(repositoryFragment.defaultBranchRef().name());
        transformedRepo.setDescription(repositoryFragment.description());
        transformedRepo.setFork(repositoryFragment.isFork());
        transformedRepo.setForksCount(repositoryFragment.forkCount());
        transformedRepo.setName(repositoryFragment.name());
        transformedRepo.setFullName(repositoryFragment.fullName());
        //transformedRepo.setGitUrl(repositoryFragment);
        //transformedRepo.setHasDownloads(repositoryFragment.);
        transformedRepo.setHasIssues(repositoryFragment.issues().totalCount() > 0);
        //transformedRepo.setHasProjects();
        //transformedRepo.setHasPages();
        transformedRepo.setHasWiki(repositoryFragment.hasWikiEnabled());
        transformedRepo.setHtmlUrl(repositoryFragment.url().toString());
        transformedRepo.setLanguage(repositoryFragment.primaryLanguage().name());
        transformedRepo.setName(repositoryFragment.name());
        transformedRepo.setOpenIssuesCount(repositoryFragment.issues().totalCount());
        final RepositoryFragment.Owner owner = repositoryFragment.owner();
        if (owner.__typename().equals("User")) {
            RepositoryFragment.AsUser userOwner = (RepositoryFragment.AsUser) owner;
            transformedRepo.setOwner(transformUser(userOwner.fragments().userFragment()));
        } else if (owner.__typename().equals("Organization")) {
            RepositoryFragment.AsOrganization organizationOwner = (RepositoryFragment.AsOrganization) owner;

            transformedRepo.setOwner(transformOrganization(organizationOwner.fragments().organizationFragment()));
        }

        transformedRepo.setUpdatedAt(repositoryFragment.updatedAt());

        if (parentFragment != null) {
            transformedRepo.setParent(transformRepository(parentFragment, null));
        }

        return transformedRepo;
    }

    private static User transformOrganization(final OrganizationFragment organizationFragment) {

        User user = new User();
        user.setAvatarUrl(organizationFragment.avatarUrl().toString());
        //user.setBlog(userFragment.);
        //user.setCompany(organizationFragment.company());
        //user.setCreatedAt(organizationFragment.createdAt());
        user.setEmail(organizationFragment.organizationEmail());
        //user.setFollowers(organizationFragment.followers().totalCount());
        //user.setFollowing(organizationFragment.following().totalCount());
        //user.setLocation(organizationFragment.location());
        user.setId(organizationFragment.id());
        user.setLogin(organizationFragment.login());
        user.setName(organizationFragment.name());
        //user.setPublicGists(organizationFragment.publicGists().totalCount());
        //user.setPublicRepos(organizationFragment.publicRepos().totalCount());

        return user;
    }
}


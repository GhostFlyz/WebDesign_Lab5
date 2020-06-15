git filter-branch --env-filter '
if [ "$GIT_AUTHOR_NAME" = "siromomo" ];
then
    GIT_AUTHOR_NAME="Tian Jiahe";
fi
if [ "$GIT_COMMITTER_NAME" = "siromomo" ];
then
    GIT_COMMITTER_NAME="Tian Jiahe";
fi
' -- --all
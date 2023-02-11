import React, {useState} from "react";
import {UserStatus} from "../../generated/openapi";
import {ActionSheet, DotLoading, InfiniteScroll, List, Space, Tag, Toast} from "antd-mobile";
import api from "../../api";

interface User {
  id: string
  email?: string
  lastName?: string
  firstName?: string
  status?: UserStatus
  creationDate?: Date
}

const UsersPage: React.FC = () => {

  const [users, setUsers] = useState<User[]>([])
  const [hasMore, setHasMore] = useState(true)

  const renderStatusTag = (user: User) => {
    const status = user.status
    let color
    if (status === UserStatus.New) {
      color = "primary"
    } else if (status === UserStatus.ToBeApproved) {
      color = "warning"
    } else if (status === UserStatus.Approved) {
      color = "success"
    } else if (status === UserStatus.Banned) {
      color = "default"
    } else {
      color = "default"
    }
    debugger
    return <Tag color={color} fill="outline">{status}</Tag>
  }

  const listActions = (user: User) => {
    const items = []
    const status = user.status

    if (status === UserStatus.Banned) {
      items.push({
        text: 'Restore',
        key: "action-restore-user",
        onClick: () => api.user.restore({id: user.id})
            .then(() => {
              user.status = UserStatus.New
              updateTableRecord(user)
              Toast.show({content: "User has been restored"})
            })
            .catch((reason) => Toast.show({content: reason.message}))
      })
    } else {
      items.push({
        text: 'Ban',
        key: "action-ban-user",
        onClick: () =>
            api.user.ban({id: user.id})
                .then(() => {
                  user.status = UserStatus.Banned
                  updateTableRecord(user)
                  Toast.show({content: "User has been banned", icon: "success"})
                })
                .catch((reason) => Toast.show({content: reason.message}))
      })
    }

    return items
  }

  const loadMore = async () => {
    let cursorPosition
    if (users.length > 0) {
      cursorPosition = users[users.length - 1].creationDate
    }
    let append = await api.user.getAllUsers({limit: 30, cursorPosition: cursorPosition})
        .then(request => request)

    setUsers(val => [...val, ...append])
    setHasMore(append.length > 0)
  }

  const updateTableRecord = (record: User) => {
    const index = users.findIndex((user) => record.id === user.id)
    if (index > -1) {
      const newData = [...users]
      const item = newData[index]
      newData.splice(index, 1, {
        ...item,
        ...record,
      })
      setUsers(newData)
    }
  }

  return (
      <div>
        <List>
          {users.map((user) => (
              <List.Item
                  key={user.id}
                  onClick={() => ActionSheet.show({actions: listActions(user)})}
              >
                <Space justify="center">
                  <span style={{verticalAlign: "middle"}}>{user.id}</span>
                  {renderStatusTag(user)}
                </Space>
              </List.Item>
          ))
          }
        </List>
        <InfiniteScroll loadMore={loadMore} hasMore={hasMore}>
          <div>
            {hasMore && <>
              <span>Loading</span>
              <DotLoading/>
            </>}
          </div>
        </InfiniteScroll>
      </div>
  )
};

export default UsersPage;

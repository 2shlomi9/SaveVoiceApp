# SafeVoice: Group Voice Messaging App for Shabbat Observance during War

## Overview
SafeVoice is a specialized group voice messaging application tailored for individuals observing Shabbat who seek to stay informed about ongoing war situations without violating Shabbat laws. By streaming voice messages directly to users' phones without requiring physical interaction or device activation, SafeVoice ensures users can stay updated while adhering to Shabbat restrictions.

## Features
1. **Send Voice Messages:**
    - *Description:* Managers can record and send voice messages to groups.

2. **Manage Group Membership:**
    - *Description:* Administrators can invite users to join groups and approve membership requests.

3. **Listen to Burst Recordings:**
    - *Description:* Group users can listen to burst recordings initiated by managers in real-time.

## Additional Functionalities
- **Create and Close Groups:**
    - *Description:* Managers can create groups and close them if necessary.
- **Recording Voice Messages:**
    - *Description:* Managers can record voice messages for group communication.
- **User Entry and Approval:**
    - *Description:* Users can request entry to groups, requiring approval from administrators.
- **Joining and Leaving Groups:**
    - *Description:* Users can join groups and leave them as needed.
- **Receive Voice Messages:**
    - *Description:* Users receive voice messages sent by managers.

## System Model
- **Message Delivery Model:**
    - Messages are detected by a listener in the database and streamed to relevant groups.
    - Playback of messages occurs in real-time, with acknowledgments sent to the sender afterward.
    - Message playback follows a queue-based system to ensure order.

## Object Model
- **Group Membership:**
    - Users can belong to multiple groups, managed by administrators.
    - Each group must have at least one manager and one member.
- **Voice Recordings:**
    - Recordings are associated with specific groups, and only administrators can send recordings within groups.

## Assumptions
- Users can belong to multiple groups simultaneously.
- Group membership requests require approval from administrators.
- Only administrators have the authority to send recordings within groups.

## Conclusion
SafeVoice offers a reliable platform for group communication through voice messages, tailored for Shabbat observance during critical situations such as war. With its innovative approach to voice message streaming, SafeVoice ensures users can stay connected and informed while upholding their religious observance.
